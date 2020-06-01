package com.lch;

import com.lch.dbpool.pool.DbPool;
import com.lch.domain.Student;
import com.lch.service.StudentService;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestMain {
    StudentService service = new StudentService();

    @Test
    public void t1() {
        for(int i = 3 ; i < 10 ; i++) {
            Student student = new Student(i, "李春宏"+i, 19, "nan");
            service.regist(student);
        }
    }
    @Test
    public void t2() {
        service.delete(1);
    }
    @Test
    public void t3() {
        Student student = new Student(11, "李春宏"+11, 20, "女");
        service.update(student);
    }
    @Test
    public void t4() {
        Map student = new HashMap<>();
        student.put("sid",10);
        student.put("sname","鑫儿");
        student.put("sage",19);
        student.put("ssex","不男不女");
        service.regist(student);
    }
    @Test
    public void t5() {
        Student student = service.findOne(9);
        System.out.println(student);
    }

    @Test
    public void t6() {
        Map student = service.findOneByMap(2);
        System.out.println(student);
    }

}
