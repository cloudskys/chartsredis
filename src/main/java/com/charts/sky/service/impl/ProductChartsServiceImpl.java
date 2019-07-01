package com.charts.sky.service.impl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.charts.sky.entity.ProductCharts;
import com.charts.sky.service.ProductChartsService;
import com.charts.sky.util.Constants;
import com.charts.sky.util.StringUtil;
import com.charts.sky.vo.DynamicVO;
import com.charts.sky.vo.ProductChartsVO;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProductChartsServiceImpl implements ProductChartsService {

    List<ProductCharts> phones = Arrays.asList(new ProductCharts(1, "苹果"),
            new ProductCharts(2, "小米"),
            new ProductCharts(3, "华为"),
            new ProductCharts(4, "一加"),
            new ProductCharts(5, "vivo"));

    Jedis jedis = null;
    @PostConstruct
    public void initRedisClient () {
        // 注：保持代码简介，未使用 JedisPool 生产环境应使用连接池
        jedis = new Jedis("192.168.199.197", 6379);
    }

    @Override
    public void buyPhone(int phoneId) {
        // 购买成功则对排行榜中该手机的销量进行加1
        jedis.zincrby(Constants.SALES_LIST, 1, String.valueOf(phoneId));

        // 添加购买动态
        long currentTimeMillis = System.currentTimeMillis();
        String msg = currentTimeMillis + Constants.separator + phones.get(phoneId - 1).getName();
        jedis.lpush(Constants.BUY_DYNAMIC, msg);
    }

    @Override
    public List<ProductChartsVO> getPhbList() {
        // 按照销量多少排行，取出前五名
        Set<Tuple> tuples = jedis.zrevrangeWithScores(Constants.SALES_LIST, 0, 4);
        List<ProductChartsVO> list = new ArrayList<>();
        for (Tuple tuple : tuples) {
        	ProductChartsVO vo = new ProductChartsVO();
            // 取出对应 phoneId 的手机名称
            int phoneId = Integer.parseInt(tuple.getElement());
            vo.setName(phones.get(phoneId - 1).getName());
            vo.setSales((int) tuple.getScore());
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<DynamicVO> getBuyDynamic() {

        List<DynamicVO> dynamicList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String result = jedis.lindex(Constants.BUY_DYNAMIC, i);
            if (StringUtils.isEmpty(result)) {
                break;
            }
            String[] arr = result.split(Constants.separator);
            long time = Long.valueOf(arr[0]);
            String phone = arr[1];
            DynamicVO vo = new DynamicVO();
            vo.setPhone(phone);
            vo.setTime(StringUtil.showTime(new Date(time)));
            dynamicList.add(vo);
        }

        // 只保留队列中20个销售动态
        jedis.ltrim(Constants.BUY_DYNAMIC, 0, 19);
        return dynamicList;
    }

    @Override
    public int phoneRank(int phoneId) {
        // 如果是排名第一， 返回的是0 ，因此如果为null 即不在排行榜上则返回-1
        Long zrank = jedis.zrevrank(Constants.SALES_LIST, String.valueOf(phoneId));
        return zrank == null ? -1 : zrank.intValue();
    }

    @Override
    public void clear() {
        jedis.del(Constants.SALES_LIST);
        jedis.del(Constants.BUY_DYNAMIC);
    }

    @Override
    public void initCache() {
        Map<String, Double> map = new HashMap<>();
        map.put("1", 4.0);
        map.put("2", 2.0);
        map.put("3", 3.0);
        jedis.zadd(Constants.SALES_LIST, map);
    }
}
