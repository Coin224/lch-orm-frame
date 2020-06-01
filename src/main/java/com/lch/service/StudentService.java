package com.lch.service;

import com.lch.dao.StudentDao;
import com.lch.domain.Student;

import java.sql.SQLException;
import java.util.Map;

public class StudentService {


    private StudentDao dao = new StudentDao();

    public void regist(Student student) {
        try {
            dao.insert(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void regist(Map map) {
        try {
            dao.insert(map);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void delete(Integer sid) {
        dao.delete(sid);
    }

    public void update(Student student) {
        dao.update(student);
    }
}
