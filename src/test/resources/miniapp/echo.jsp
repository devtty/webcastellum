<%@page contentType="text/html" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <title>Echo (in MiniApp)</title>
    </head>
    <body bgcolor="#ffffcc">
        <h2>Echo (in MiniApp)</h2>



        <table border="0" width="100%">
            <tr>
                <td width="250">

                    <%@include file="WEB-INF/jspf/menu.jspf"%>

                </td>
                <td align="left" valign="top">



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

                    <hr/>
                    <img src="test.png"/>


                </td>
            </tr>
        </table>


    
        <%@include file="WEB-INF/jspf/content.jspf"%>
    </body>
</html>
