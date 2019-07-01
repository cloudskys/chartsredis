package com.charts.sky.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.charts.sky.entity.ProductCharts;
import com.charts.sky.service.ProductChartsService;
import com.charts.sky.vo.DynamicVO;
import com.charts.sky.vo.ProductChartsVO;

import java.util.Arrays;
import java.util.List;

@Controller
public class ProductChartsController {
    List<ProductCharts> phones = Arrays.asList(new ProductCharts(1, "苹果"),
            new ProductCharts(2, "小米"),
            new ProductCharts(3, "华为"),
            new ProductCharts(4, "一加"),
            new ProductCharts(5, "vivo"));

    @Autowired
    private ProductChartsService phoneService;

    @RequestMapping("/")
    public ModelAndView home() {
        for (ProductCharts phone : phones) {
            int ranking = phoneService.phoneRank(phone.getId()) + 1;
            phone.setRanking(ranking == 0 ? "榜上无名" : "销量排名：" + ranking);
        }

        ModelAndView view = new ModelAndView("index");
        view.addObject("phones", phones);

        List<ProductChartsVO> phbList = phoneService.getPhbList();
        List<DynamicVO> dynamicList = phoneService.getBuyDynamic();
        view.addObject("dynamicList", dynamicList);
        view.addObject("phbList", phbList);
        return view;
    }

    /**
     * 模拟购买手机
     *
     * @param phoneId
     * @return
     */
    @RequestMapping("/buy/{phoneId}")
    public String buyPhone(@PathVariable int phoneId) {
        phoneService.buyPhone(phoneId);
        return "redirect:/";
    }

    /**
     * 获得指定手机的排名
     *
     * @param phoneId
     * @return
     */
    @RequestMapping("/ranking/{phoneId}")
    @ResponseBody
    public int phoneRanking(@PathVariable int phoneId) {
        return phoneService.phoneRank(phoneId);
    }

    /**
     * 清空缓存
     * @return
     */
    @RequestMapping("/clear")
    public String clear() {
        phoneService.clear();
        return "redirect:/";
    }
}
