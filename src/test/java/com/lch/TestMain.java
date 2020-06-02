package com.lch;

import com.lch.domain.Student;
import com.lch.service.StudentService;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMain {
    StudentService service = new StudentService();

    @Test
    public void t1() {
        for(int i = 25 ; i < 40 ; i++) {
            Student student = new Student(i, "李春宏"+i, 1+i, "nan");
            service.regist(student);
        }
    }
    @Test
    public void t2() {
        service.delete(11);
    }
    @Test
    public void t3() {
        Student student = new Student(11, "李春宏"+11, 20, "女");
        service.update(student);
    }
    @Test
    public void t4() {
        Map student = new HashMap<>();
        student.put("sid",11);
        student.put("sname","鑫儿11");
        student.put("sage",21);
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


    @Test
    public void t7() {
        List<Student> students = service.findList();
        System.out.println(students);
    }
}
