<!doctype html>

<html>
  <head>
    <title>Web Castellum Test JSP</title>
</head>
<body>
   <%= request.getRemoteHost() %>

   <div id="firstnameParameter">Parameter: <%= request.getParameter("fname") %></div>

<form action="test.jsp" method="POST">
    <label for="fname">First name:</label><br>
    <input type="text" id="fname" name="fname"><br>
    <label for="lname">Last name:</label><br>
    <input type="text" id="lname" name="lname">
    <label for="check1"> Check this!</label><br>
    <input type="checkbox" id="check1" name="check1" value="Check">
    <input type="submit" id="submit">
</form> 
  
</body>
</html>
