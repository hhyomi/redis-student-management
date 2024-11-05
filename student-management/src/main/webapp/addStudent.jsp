<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>新增学生</title>
</head>
<body>
<h2>新增学生</h2>
<form action="students" method="post">
    <input type="hidden" name="id" />
    姓名: <input type="text" name="name" required /><br/>
    出生日期: <input type="date" name="birthday" required /><br/>
    备注: <input type="text" name="description" /><br/>
    平均分: <input type="number" name="avgScore" required /><br/>
    <input type="submit" value="提交" />
</form>
<a href="students">返回学生列表</a>
</body>
</html>