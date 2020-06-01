package com.lch.test;

import com.lch.dbpool.pool.DbPool;
import com.lch.domain.Student;
import com.lch.service.StudentService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestMain {


    public static void main(String[] args) throws SQLException {
        StudentService service = new StudentService();
        for(int i = 3 ; i < 10 ; i++) {
            Student student = new Student(i, "李春宏"+i, 19, "nan");

//        Map student = new HashMap<>();
//        student.put("sid",2);
//        student.put("sname","haha");
//        student.put("sage",19);
//        student.put("ssex","女");

            service.regist(student);
        }
        //service.delete(1);

        //service.update(student);
    }
}
