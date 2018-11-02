<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="baseUrl" value="${pageContext.request.contextPath}"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>文件</title>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/basic.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/index.css">
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/topAndBottom.css">
    <script type="text/javascript" src="${baseUrl}/js/jquery-2.1.0.js"></script>
    <script type="text/javascript">

        function searchData() {
            var fileName = document.getElementById("s_file_name").value;
            window.location = "${baseUrl}/file/list.do?fileName=" + fileName;
        }
    </script>
</head>
<body>

<div class="content clearfloat">
    <div class="content_right fr">
        <div class="first clearfloat">
            <div class="fl title">文件</div>
            <div class="fr operation">
                <span><a href="${baseUrl}/file/list.do?fileName=${fileName}" target="rightFrame"> <img src="${baseUrl}/img/flush.png">刷新</a></span>
            </div>
        </div>

        <div class="second">
            <form>
                <span>文件名：</span>
                <input style="width: 120px; height: 26px;background-color: #ffffff;color:#000;border: 1px solid #EAEBE8;cursor: auto;" type="text" id="s_file_name" value="${fileName}"/>
                <input type="button" onclick="searchData();" value="搜索"/>
            </form>
        </div>

        <div class="third">
            <table>
                <tr>
                    <th width="4%">序号</th>
                    <th width="15%">客户端IP</th>
                    <th width="15%">路径</th>
                    <th width="15%">名称</th>
                    <th width="12%">大小</th>
                    <th width="15%">文件状态</th>
                    <th width="12%">上传时间</th>
                    <th style="text-align: center;">操作</th>
                </tr>
                <c:forEach items="${files}" var="file" varStatus="vs">
                    <tr id="tr_${file.id}">
                        <td>${vs.index + 1}</td>
                        <td>${file.clientIpAddress}</td>
                        <td class="self_th_td" style="max-width: 200px;">${file.filePath}</td>
                        <td class="self_th_td" style="max-width: 200px;">${file.fileName}</td>
                        <td>${file.fileSize}</td>
                        <td>
                            <c:if test="${file.fileStatus == 0}">未上传</c:if>
                            <c:if test="${file.fileStatus == 1}">已上传</c:if>
                        </td>
                        <td>
                            <c:if test="${file.createTime > 0}">
                                <jsp:useBean id="createTime" class="java.util.Date"/>
                                <jsp:setProperty name="createTime" property="time" value="${file.createTime}"/>
                                <fmt:formatDate value="${createTime}" pattern="yyyy-MM-dd HH:mm"/>
                            </c:if>
                        </td>
                        <td style="text-align: center;">
                            <span><a href="${baseUrl}/file/download.do?id=${file.id}" target="rightFrame"><img src="${baseUrl}/img/download.png">下载</a></span>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <div class="fenye">
                <ul class="fy_ul">
                    <li class="fy_li_first">共 ${count} 条记录</li>
                    <c:if test="${currPage == 1}">
                        <li><a href="#">上一页</a></li>
                    </c:if>
                    <c:if test="${currPage > 1}">
                        <li><a href="${baseUrl}/file/list.do?fileName=${fileName}&page=${currPage - 1}">上一页</a></li>
                    </c:if>
                    <li id="currPage">${currPage}</li>
                    <c:if test="${currPage >= totalPage}">
                        <li><a href="#">下一页</a></li>
                    </c:if>
                    <c:if test="${currPage < totalPage}">
                        <li><a href="${baseUrl}/file/list.do?fileName=${fileName}&page=${currPage + 1}">下一页</a></li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>