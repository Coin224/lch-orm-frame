package com.lch.service;

import com.lch.dao.StudentDao;
import com.lch.domain.Student;
import com.lch.orm.SqlSession;

import java.util.List;
import java.util.Map;

public class StudentService {

    //原来的真实的dao
//    private StudentDao dao = new StudentDao();
    // 得到的代理
    private StudentDao dao = new SqlSession().getMapper(StudentDao.class);

    public void regist(Student student) {
        dao.insert(student);
    }

    public void regist(Map map) {
        dao.insert(map);
    }



    public void delete(Integer sid) {
        dao.delete(sid);
    }

    public void update(Student student) {
        dao.update(student);
    }

    public Student findOne(int sid) {
        return dao.findOne(sid);
    }
    public Map findOneByMap(int sid) {
        return dao.findOneByMap(sid);
    }


    public List<Student> findList() {
        return dao.findList();
    }
}
