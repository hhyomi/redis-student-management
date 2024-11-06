package com.biz.studentmanagement;

import com.biz.studentmanagement.entity.Student;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/***
 * 学生信息管理
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {
    private JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 100, "123456");

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取请求的动作类型
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            handleDelete(request, response);
        } else if ("toedit".equals(action)) {
            handleEdit(request, response);
        } else if ("edit".equals(action)) {
            saveStudent(request,response);
        } else if ("add".equals(action)) {
            saveStudent(request,response);
        }
        else{
            handleListStudents(request, response);
        }
    }


    /***
     * 删除学生
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        deleteStudent(id);
        response.sendRedirect("students");
    }

    /***
     * 编辑学生信息
     */
    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        Student student = getStudent(id);
        request.setAttribute("student", student);
        request.getRequestDispatcher("editStudent.jsp").forward(request, response);
    }

    /***
     * 学生list
     */
    private void handleListStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Student> students = getAllStudents();
        Collections.sort(students, Comparator.comparingInt(Student::getAvgScore).reversed());

        // 处理分页
        int page ;
        if(request.getParameter("page")==null||"".equals(request.getParameter("page"))){
            page = 1;
        }else {
            page = Integer.parseInt(request.getParameter("page"));
        }

        int pageSize = 10;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, students.size());

        request.setAttribute("students", students.subList(fromIndex, toIndex));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", (int) Math.ceil(students.size() *1.0 / pageSize ));
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /***
     * 保存学生信息（创建或更新）
     */
    private void saveStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id").equals("")?UUID.randomUUID().toString():request.getParameter("id");
        String name = request.getParameter("name");
        String birthday = request.getParameter("birthday");
        String description = request.getParameter("description");
        int avgScore = Integer.parseInt(request.getParameter("avgScore"));

        Student student = new Student();
        student.setId(id);
        student.setName(name);

        // 日期转换
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            student.setBirthday(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        student.setDescription(description);
        student.setAvgScore(avgScore);

        // 保存学生信息
        saveStudentToRedis(student);
        response.sendRedirect("/students");
    }

    /***
     * 将学生信息保存到 Redis 数据库。
     */
    private void saveStudentToRedis(Student student) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset("student:" + student.getId(), "name", student.getName());
            jedis.hset("student:" + student.getId(), "birthday", String.valueOf(student.getBirthday().getTime()));
            jedis.hset("student:" + student.getId(), "description", student.getDescription());
            jedis.hset("student:" + student.getId(), "avgScore", String.valueOf(student.getAvgScore()));
        }
    }

    /***
     * 从 Redis 数据库中删除学生信息。
     */
    private void deleteStudent(String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("student:" + id);
        }
    }

    /***
     * 根据 ID 从 Redis 数据库中获取学生信息。
     */
    private Student getStudent(String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> data = jedis.hgetAll("student:" + id);
            if (data.isEmpty()) {
                return null;
            }
            Student student = new Student();
            student.setId(id);
            student.setName(data.get("name"));
            student.setBirthday(new Date(Long.parseLong(data.get("birthday"))));
            student.setDescription(data.get("description"));
            student.setAvgScore(Integer.parseInt(data.get("avgScore")));
            return student;
        }
    }

    /***
     * 从 Redis 数据库中获取所有学生信息。
     */
    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("student:*");
            for (String key : keys) {
                Student student = getStudent(key.replace("student:", ""));
                if (student != null) {
                    students.add(student);
                }
            }
        }
        return students;
    }
}
