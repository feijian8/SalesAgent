<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" media="all" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Авторизация</title>
<script type="text/javascript"  src="${pageContext.request.contextPath}/js/jquery-latest.js" ></script>
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


</head>

<body>
	
	
	<c:if test="${invalidUser != null}">
		<div class="errorblock">
			<c:out value="${invalidUser}"/>
		</div>
	</c:if>
	
	
	<form:form methodParam="POST" modelAttribute="atribute" action="login" method='POST'>
		<center>
				<div class="justtext">Login:</div>
				<c:choose>
				    <c:when test="${user_login!=null}">
				    	<input type='text' class="selement" name='username' value='<c:out value="${user_login}"/>'/>
				    </c:when>
				    <c:otherwise>
				    	<input type='text' class="selement" name='username' />
				    </c:otherwise>
			    </c:choose>
				<div class="justtext">Password:</div>
				<input type='password' class="selement" name='password' />
				<input type="submit" class="selement" value="OK">
		</center>
	</form:form>
	
	
	
</body>
</html>