<%@page contentType="text/html" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <%
            Long counter = (Long) session.getAttribute("SOME_SAMPLE_COUNTER");
            if (counter == null) counter = new Long(0);
            counter = new Long(counter.longValue() + Long.parseLong(request.getParameter("by")));
            session.setAttribute("SOME_SAMPLE_COUNTER", counter);
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <meta http-equiv="refresh" content="5;url=<%=response.encodeURL("echo.jsp")%>">
        <title>MiniApp - Increment</title>
    </head>
    <body bgcolor="#ffffcc">
            Incremented counter. <%-- Now redirecting to echo page in 5 seconds. --%>
            <hr/>
            Direct link to <a href="<%=response.encodeURL("echo.jsp")%>">echo</a> page
            <%@include file="WEB-INF/jspf/content.jspf"%>
    </body>
</html>
