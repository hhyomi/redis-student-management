<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>新增学生</title>
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
            height: 100vh;
        }

        h2 {
            color: #333;
            margin-bottom: 20px;
        }

        .container {
            width: 100%;
            max-width: 400px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
        }

        input[type="text"],
        input[type="date"],
        input[type="number"],
        input[type="submit"] {
            width: 100%;
            padding: 10px;
            margin: 8px 0 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #007BFF;
            color: white;
            border: none;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #0056b3;
        }

        a {
            display: inline-block;
            margin-top: 10px;
            text-decoration: none;
            color: #007BFF;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>新增学生</h2>
    <form action="${pageContext.request.contextPath}/students?action=add" method="post" >
        <input type="hidden" name="id" />
        <label for="name">姓名:</label>
        <input type="text" id="name" name="name" required />

        <label for="birthday">出生日期:</label>
        <input type="date" id="birthday" name="birthday" required />

        <label for="description">备注:</label>
        <input type="text" id="description" name="description" />

        <label for="avgScore">平均分:</label>
        <input type="number" id="avgScore" name="avgScore" required />

        <input type="submit" value="提交" />
    </form>
    <a href="students">返回学生列表</a>
</div>

</body>
</html>
