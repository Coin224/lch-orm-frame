package com.lch.orm;


import com.lch.orm.annotation.Delete;
import com.lch.orm.annotation.Insert;
import com.lch.orm.annotation.Select;
import com.lch.orm.annotation.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            StatementAndConnection statementAndConnection = handler.handlerSqlAndParam(connection,sql,obj,statement);
            connection = statementAndConnection.getConnection();
            statement = statementAndConnection.getStatement();
            // 4.执行操作
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeAll(connection,statement,null);
        }
    }

    // 设计一个方法 查询一条记录
    // 参数：sql + 参数 + 查询结果组成的类型
    public <T>T findOne(String sql ,Object obj , Class resultType) {
        return (T) findList(sql,obj,resultType).get(0);
//        Object result = null;
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        try {
//            // 1.解析sql
//            // 2.获取连接池、获取连接
//            // 3.获取状态参数
//            StatementAndConnection statementAndConnection = handler.handlerSqlAndParam(connection,sql,obj,statement);
//            connection = statementAndConnection.getConnection();
//            statement = statementAndConnection.getStatement();
//            // 4.执行操作
//            resultSet = statement.executeQuery();
//            // 5.先判断有没有结果 如果有处理结果
//            if (resultSet.next()) {
//                result = handler.handlerResult(resultSet, resultType);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            handler.closeAll(connection,statement,resultSet);
//        }
//        return (T) result;
    }




    public <T> List<T> findList(String sql , Class resultType) {
        return this.findList(sql,null,resultType);
    }
    // 查询多条记录
    private <T> List<T> findList(String sql , Object obj , Class resultType) {
        List<T> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 1.解析sql
            // 2.获取连接池、获取连接
            // 3.获取状态参数
            StatementAndConnection statementAndConnection = handler.handlerSqlAndParam(connection,sql,obj,statement);
            connection = statementAndConnection.getConnection();
            statement = statementAndConnection.getStatement();
            // 4.执行操作
            resultSet = statement.executeQuery();
            // 5.先判断有没有结果 如果有处理结果
            while (resultSet.next()) {
                result.add(handler.handlerResult(resultSet, resultType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeAll(connection,statement,resultSet);
        }
        return result;
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

    //写一个获得代理的方法
    public <T>T getMapper(Class clazz) {
        //三个参数 Classloader clazz数组 invocationHandler
        ClassLoader classLoader = clazz.getClassLoader();
        Class[] interfaces = new Class[]{clazz};
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1.获取方法上面的注解
                Annotation methodAnnotation = method.getAnnotations()[0];
                // 2.获取注解的的类型
                Class annotationType = methodAnnotation.annotationType();
                // 3.获取该注解的value方法
                Method annotationMethod = annotationType.getDeclaredMethod("value");
                // 4.执行方法，获取到sql
                String sql = (String) annotationMethod.invoke(methodAnnotation);

                // 5.分析参数
                Object param = (args == null) ? null : args[0];
                if (annotationType == Insert.class) {
                    SqlSession.this.insert(sql,param);
                } else if(annotationType == Update.class) {
                    SqlSession.this.update(sql,param);
                } else if (annotationType == Delete.class) {
                    SqlSession.this.delete(sql,param);
                } else if (annotationType == Select.class) {
                    //这里是查询的方法
                    // 获取方法的返回值类型 -- 来代替原来的resultType
                    // 获取返回值类型 看是查多条还是查一条
                    Class returnType = method.getReturnType();
                    if (returnType == List.class) {
                        // 查多条
                        // 获取泛型的类型 是一个接口 要把这个类型还原成 真实的泛型
                        Type innerReturnType = method.getGenericReturnType();
                        ParameterizedType realReturnType = (ParameterizedType) innerReturnType;
                        // 继续反射这个类型中所有的泛型
                        // 获取所有的泛型
                        Type[] patternTypes = realReturnType.getActualTypeArguments();
                        // 我们只要第一个
                        Type patternType = patternTypes[0];
                        //将这个泛型类还原成Class
                        Class realPatternType = (Class) patternType;
                        return SqlSession.this.findList(sql,param,realPatternType);
                    } else {
                        // 查单条
                        return SqlSession.this.findOne(sql,param,returnType);
                    }
                    
                }
                return null;
            }
        };
        return (T) Proxy.newProxyInstance(classLoader,interfaces,invocationHandler);
    }

}
