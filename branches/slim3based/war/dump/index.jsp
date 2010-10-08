<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Dump</title>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
google.load("jquery", "1.4.2");
function initialize() {
	$("#checkall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', true);
        return false; 
	});
	$("#uncheckall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', false);
        return false; 
	});
}
google.setOnLoadCallback(initialize);
</script>
</head>
<body>
<div style="position:absolute; top:10px; right:10px;"><a href="${f:url("/logout")}">logout</a></div>
<div>Please select kind(s) to dump.</div>
<form action="${f:url("start")}" method="POST" id="form1">
<input type="button" value="Check All" id="checkall" />
<input type="button" value="Uncheck All" id="uncheckall" />
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="kindArray" value="${row.name}" id="kind_${row.name}" />
  <label for="kind_${row.name}">${row.name}&nbsp;/&nbsp;<fmt:formatNumber>${row.count}</fmt:formatNumber> records</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="execute" />
</form>
</body>
</html>
