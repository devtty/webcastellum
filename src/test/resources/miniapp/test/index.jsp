<%@page contentType="text/html" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <title>MiniApp</title>
    </head>
    <body bgcolor="#ffffcc">

        <h2>TEST</h2>


                    <%-- some deliberately unsafe sample code to directly print session-id and all parameters --%>

                    Session-ID
                    <%= session.getId() %>

                    <hr/>

                    Counter in session:
                    <%= session.getAttribute("SOME_SAMPLE_COUNTER") %>

                    <hr/>

                    Params
                   <dl>
                        <%
                        for (final java.util.Enumeration/*<String>*/ names = request.getParameterNames(); names.hasMoreElements();) {
                        final String name = (String) names.nextElement();
                        final String[] values = request.getParameterValues(name);
                        %>
                        <dt><%=name%></dt>
                        <dd><%=(values == null) ? null : java.util.Arrays.asList(values)%></dd>
                        <%
                        }
                        %>
                    </dl>
    </body>
</html>
