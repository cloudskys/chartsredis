<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String projectPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String staticPath = projectPath + "/static";
    String imagePath = projectPath + "/uploads";
%>
<c:set var="ctxsta" value="${pageContext.request.contextPath}/static"/>