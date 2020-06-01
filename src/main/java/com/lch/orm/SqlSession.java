package com.lch.orm;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlSession {


    private Handler handler = new Handler();


    // 设计一个方法、可以进行进行增删改操作
    // 需要的参数：SQL: insert into student values(?,?,?,?)
    //                  insert into student values(#{sid},#{sname},#{sage},#{ssex})
    //            参数: 可以是一个基本数据类型、实体对象、map集合
    //                  所以参数用一个Object类型
    public void update(String sql ,Object obj) {
        Connection connection = null;
        PreparedStatement statement =  null;
        try {
            // 1.解析sql
            // 2.获取连接池、获取连接
            // 3.获取状态参数
            statement = handler.handlerSqlAndParam(connection,sql,obj,statement);
            // 4.执行操作
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 设计一个方法 查询一条记录
    // 参数：sql + 参数 + 查询结果组成的类型
    public <T>T findOne(String sql ,Object obj , Class resultType) {

        Object result = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 1.解析sql
            // 2.获取连接池、获取连接
            // 3.获取状态参数
            statement = handler.handlerSqlAndParam(connection,sql,obj,statement);
            // 4.执行操作
            resultSet = statement.executeQuery();
            // 5.先判断有没有结果 如果有处理结果
            if (resultSet.next()) {
                result = handler.handlerResult(resultSet, resultType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return (T) result;
    }



    // 写几个重载方法 增强可读性
    public void insert(String sql ,Object obj) {
        this.update(sql,obj);
    }

    public void delete(String sql ,Object obj) {
        this.update(sql,obj);
    }

    // 删除整张表
    public void delete(String sql) {
        this.update(sql,null);
    }

}
