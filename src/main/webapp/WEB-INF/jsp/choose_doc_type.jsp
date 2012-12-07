<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" media="all" />
<script type="text/javascript"  src="${pageContext.request.contextPath}/js/jquery-latest.js" ></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Выбор типа накладной</title>
<script type="text/javascript" charset="utf-8">

$( document ).ready( function() {
    var $body = $('body'); //Cache this for performance

    var setBodyScale = function() {
        var scaleFactor = 0.55,
            scaleSource = $(window).height(),
            maxScale = 600,
            minScale = 10;

        var fontSize = scaleSource * scaleFactor; //Multiply the width of the body by the scaling factor:

        if (fontSize > maxScale) fontSize = maxScale;
        if (fontSize < minScale) fontSize = minScale; //Enforce the minimum and maximums

        $('body').css('font-size', fontSize + '%');
    }

    $(window).resize(function(){
        setBodyScale();
    });

    //Fire it when the page first loads:
    setBodyScale();
});
</script>

<style>	
.line{
	font-size: xx-large;
}

.head_line{
	font-size: xx-large;
}
 </style>

  
  
  
  
</head>

<body>
 <c:if test="${shop != null}">
 	<div class="line">
 		<c:out value="${shop.code}"/>&nbsp;
 		<c:out value="${shop.name}"/>&nbsp;
 		<c:out value="${shop.address}"/>&nbsp;
 	</div>
 </c:if>
 
 		<!-- div class="head_line">Выбор типа документа</div -->
 		<a href="realization" style="text-decoration: none;"><div class="menu_button">Реализация</div></a>
 		<a href="return_back" style="text-decoration: none;"><div class="menu_button">Возврат</div></a>
 		<a href="cabinet" style="text-decoration: none;"><div class="menu_button">Завершить работу с ТТ</div></a>
</body>
</html>