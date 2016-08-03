<%@ page import="com.avaGo.gameServee.setting.Settings" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# product: http://ogp.me/ns/product#">
    <meta property="og:type"                   content="og:product" />
    <meta property="og:title"                  content="Add 10 step" />
    <meta property="og:image"                  content=<%=String.format(Settings.URL, request.getScheme() ,request.getServerName(), request.getServerPort(), Settings.IMAGES_DIR + request.getRequestURI().replace("jsp","png").replace("FB/","") )%> />
    <meta property="og:description"            content="Add 10 step " />
    <meta property="og:url"                    content=<%=String.format(Settings.URL, request.getScheme() ,request.getServerName(), request.getServerPort(), request.getRequestURI() )%> />
    <meta property="product:plural_title"      content="add 10 step" />
</head>
</html>

