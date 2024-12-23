<%@page contentType="text/html" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <title>MiniApp - Form</title>
    </head>
    <body bgcolor="#ffffcc">

        <table border="0" width="100%">
            <tr>
                <td width="250">

                    <%@include file="WEB-INF/jspf/menu.jspf"%>

                </td>
                <td align="left" valign="top">

                    <h2>simple sample form</h2>

                    <form action="<%=response.encodeURL("echo.jsp")%>" method="post">
                        <input type="hidden" name="hiddenOne" value="eins"/>
                        <input type="hidden" name="hiddenTwo" value="zwei"/>
                        <table border="0">
                            <tr><td>first name:</td><td><input type="text" name="firstName" value=""/></td></tr>
                            <tr><td>last name:</td><td><input type="text" name="lastName" value=""/></td></tr>
                            <tr><td>user id:</td><td><input type="text" name="userId" value="Test" disabled="disabled"/></td></tr>
                            <tr><td>description:</td><td><textarea name="description"></textarea></td></tr>
                            <tr><td>color:</td>
                                <td><select name="color" size="1">
                                        <option value="green">Nice Green</option>
                                        <option value="blue">Nice Blue</option>
                                        <option value="red">Nice Red</option>
                                    </select></td></tr>
                            <tr><td>numbers:</td>
                                <td><select name="numbers" size="3" multiple="multiple">
                                        <option value="7">Seven</option>
                                        <option value="9">Nine</option>
                                        <option value="11">Eleven</option>
                                    </select></td></tr>
                            <tr><td>hobbies:</td>
                                <td><input type="checkbox" name="hobbies" value="Sports"/>Sports<br/>
                                    <input type="checkbox" name="hobbies" value="Cars"/>Cars<br/>
                                    <input type="checkbox" name="hobbies" value="Computer"/>Computer</td></tr>
                            <tr><td>gender:</td>
                                <td><input type="radio" name="gender" value="Female"/>Female<br/>
                                    <input type="radio" name="gender" value="Male"/>Male</td></tr>
                            <tr><td colspan="2">
                                    <input type="reset"/>
                                    <input type="submit"/></td></tr>
                        </table>
                    </form>

                    <p/>
                    <a href="<%=response.encodeURL("echo.jsp?test=eins&test=zwei&mehr=3")%>">sample link</a>






<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<a href="test/">test/</a>
<a href="test/?aaa=bbb">test/?aaa=bbb</a>
<a href="redirect">redirect</a>


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

                </td>
            </tr>
        </table>
        
        <%@include file="WEB-INF/jspf/content.jspf"%>
    </body>
</html>
