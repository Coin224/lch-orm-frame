package com.lch.dao;

import com.lch.domain.Student;
import com.lch.orm.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从下面增删改几个方法可以看出
 * 这几个方法除了sql和参数不一样
 * 其他都差不多、冗余太多
 */
public class StudentDao {


    private SqlSession sqlSession = new SqlSession();





    public void insert(Map map)  {
        String sql = "insert into student values(#{sid},#{sname},#{sage},#{ssex})";
        sqlSession.insert(sql, map);
    }


    // 新增一条学生记录
    public void insert(Student student) {
        String sql = "insert into student values(#{sid},#{sname},#{sage},#{ssex})";
        sqlSession.insert(sql, student);
    }
//    public void insert(Student student)  {
//        // 0.新增sql
//        String sql = "insert into student values(?,?,?,?)";
//        // 1.获取连接池对象、获取连接
//        Connection connection = DbPool.getDbPool().getConnection();
//        PreparedStatement statement = null;
//        try {
//            // 2.获取状态参数、并赋值
//            statement = connection.prepareStatement(sql);
//            statement.setInt(1,student.getSid());
//            statement.setString(2,student.getSname());
//            statement.setInt(3,student.getSage());
//            statement.setString(4,student.getSsex());
//            // 3.执行操作
//            int count = statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // 4.关闭
//            try {
//                if (statement != null) {
//                    statement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    // 修改一条学生记录
    public void update(Student student) {
        String sql = "update student set sname = #{sname},sage = #{sage},ssex = #{ssex} where sid = #{sid}";
        sqlSession.update(sql, student);
    }
//    public void update(Student student) {
//        // 0.新增sql
//        String sql = "update student set sname = ?,sage = ?,ssex = ? where sid = ?";
//        // 1.获取连接池对象、获取连接
//        Connection connection = DbPool.getDbPool().getConnection();
//        PreparedStatement statement = null;
//        try {
//            // 2.获取状态参数、并赋值
//            statement = connection.prepareStatement(sql);
//            statement.setString(1,student.getSname());
//            statement.setInt(2,student.getSage());
//            statement.setString(3,student.getSsex());
//            statement.setInt(4,student.getSid());
//            // 3.执行操作
//            int count = statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // 4.关闭
//            try {
//                if (statement != null) {
//                    statement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    // 删除一条学生记录 根据sid
    public void delete(int sid) {
        String sql = "delete from student where sid = #{sid}";
        sqlSession.update(sql, sid);
    }
//    public void delete(int sid) {
//        // 0.新增sql
//        String sql = "delete from student where sid = ?";
//        // 1.获取连接池对象、获取连接
//        Connection connection = DbPool.getDbPool().getConnection();
//        PreparedStatement statement = null;
//        try {
//            // 2.获取状态参数、并赋值
//            statement = connection.prepareStatement(sql);
//            statement.setInt(1,sid);
//            // 3.执行操作
//            int count = statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // 4.关闭
//            try {
//                if (statement != null) {
//                    statement.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    // 查询一条学生记录
    public Student findOne(int sid) {
        String sql = "select sid,sname,sage,ssex from student where sid = #{sid}";
        return sqlSession.findOne(sql,sid,Student.class);
    }


    // 查询一条学生记录
    public Map findOneByMap(int sid) {
        String sql = "select sid,sname,sage,ssex from student where sid = #{sid}";
        return sqlSession.findOne(sql,sid, HashMap.class);
    }


    // 查询多条学生记录
    public List<Student> findList() {
        return null;
    }

}
