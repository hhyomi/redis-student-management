package com.biz.studentmanagement;


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
 * 访问学生信息页面
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {
//    private JedisPool jedisPool = new JedisPool("localhost", 6379);
    private JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),"localhost", 6379,100,"123456");


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            deleteStudent(id);
            response.sendRedirect("students");
            return;
        } else if ("edit".equals(action)) {
            String id = request.getParameter("id");
            Student student = getStudent(id);
            request.setAttribute("student", student);
            request.getRequestDispatcher("editStudent.jsp").forward(request, response);
            return;
        }

        List<Student> students = getAllStudents();
        Collections.sort(students, Comparator.comparingInt(Student::getAvgScore).reversed());

        int page;
        if (request.getParameter("page") == null) {
            page = 1;
        }else{
            page = Integer.parseInt(request.getParameter("page"));
        }

        int pageSize = 10;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, students.size());

        request.setAttribute("students", students.subList(fromIndex, toIndex));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", (int) Math.ceil(students.size() / (double) pageSize));
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String birthday = request.getParameter("birthday");
        String description = request.getParameter("description");
        int avgScore = Integer.parseInt(request.getParameter("avgScore"));

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        Student student = new Student();
        student.setId(id);
        student.setName(name);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            student.setBirthday(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        student.setDescription(description);
        student.setAvgScore(avgScore);
        
        saveStudent(student);
        response.sendRedirect("students");
    }

    private void saveStudent(Student student) {
        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.auth("123456");

            jedis.hset("student:" + student.getId(), "name", student.getName());
            jedis.hset("student:" + student.getId(), "birthday", String.valueOf(student.getBirthday().getTime()));
            jedis.hset("student:" + student.getId(), "description", student.getDescription());
            jedis.hset("student:" + student.getId(), "avgScore", String.valueOf(student.getAvgScore()));
        }
    }

    private void deleteStudent(String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("student:" + id);
        }
    }

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
