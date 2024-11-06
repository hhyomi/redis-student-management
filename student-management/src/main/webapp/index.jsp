<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生数据管理</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }

        h2 {
            color: #333;
        }

        .container {
            width: 80%;
            max-width: 800px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }

        a {
            text-decoration: none;
            color: #007BFF;
            margin-bottom: 10px;
            display: inline-block;
        }

        a:hover {
            text-decoration: underline;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
            color: #333;
        }

        tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .pagination {
            text-align: center;
            margin-top: 20px;
        }

        .pagination a {
            margin: 0 5px;
            padding: 5px 10px;
            border: 1px solid #007BFF;
            border-radius: 4px;
            color: #007BFF;
        }

        .pagination a:hover {
            background-color: #007BFF;
            color: white;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>学生列表</h2>
    <a href="addStudent.jsp">新增学生</a>
    <table>
        <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>出生日期</th>
            <th>备注</th>
            <th>平均分</th>
            <th>操作</th>
        </tr>
        <c:forEach var="student" items="${students}">
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td><fmt:formatDate pattern="yyyy-MM-dd" value="${student.birthday}"/></td>
                <td>${student.description}</td>
                <td>${student.avgScore}</td>
                <td>
                    <a href="students?action=edit&id=${student.id}">修改</a>
                    <a href="students?action=delete&id=${student.id}" onclick="return confirm('确定要删除吗?')">删除</a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <div class="pagination">
        <c:if test="${currentPage > 1}">
            <a href="students?page=${currentPage - 1}">上一页</a>
        </c:if>
        <c:forEach var="i" begin="1" end="${totalPages}">
            <a href="students?page=${i}">${i}</a>
        </c:forEach>
        <c:if test="${currentPage < totalPages}">
            <a href="students?page=${currentPage + 1}">下一页</a>
        </c:if>
    </div>
</div>

</body>
</html>
