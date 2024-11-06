<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.biz.studentmanagement.Student" %>
<html>
<head>
    <title>修改学生</title>
</head>
<body>
<h2>修改学生</h2>
<%
    Student student = (Student) request.getAttribute("student");
%>
<form action="students" method="post" >
    <input type="hidden" name="id" value="${student.id}" />
    姓名: <input type="text" name="name" value="${student.name}" required /><br/>
    出生日期: <input type="date" name="birthday" value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(student.getBirthday()) %>" required /><br/>
    备注: <input type="text" name="description" value="${student.description}" /><br/>
    平均分: <input type="number" name="avgScore" value="${student.avgScore}" required /><br/>
    <input type="submit" value="提交" />
</form>
<a href="students">返回学生列表</a>
</body>
</html>