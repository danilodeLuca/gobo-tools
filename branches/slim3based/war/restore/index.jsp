<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
</head>
<body>
<div style="position:absolute; top:10px; right:10px;"><a href="${f:url("/logout")}">logout</a></div>
<div>Please select a spreadsheet to restore.</div>
<ul>
<c:forEach items="${list}" var="row">
<li><a href="${f:url("sheet")}?ssKey=${row.key}">${f:h(row.title)}</a></li>
</c:forEach>
</ul>
</body>
</html>