package com.lch.orm;


import com.lch.dbpool.pool.DbPool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Handler {


    // 写一个方法来解析sql
    private SqlAndKey parseSql(String sql) {
        // 1.首先解析sql 把#{}换成?
        StringBuilder newSql = new StringBuilder();//频繁拼接sql

        // 2.把里面的值取出来与参数中的值对应
        // 把 sid sname sage ssex取出来放在一个list集合中 当成key
        // list集合有序 可以和?对应
        // 按顺序取出来可以当成key从对象或者map里面取值与?对应然后赋值
        List<String> keyList = new ArrayList<>();

        while(true) {
            // 找到成对出现的#{ 和 }的位置
            int leftIndex = sql.indexOf("#{");
            int rightIndex = sql.indexOf("}");
            // 严谨性判断
            // 如果#{ 和 } 存在 并且 左边坐标小于右边坐标
            if (leftIndex != -1 && rightIndex != -1 && leftIndex <rightIndex) {
                newSql.append(sql.substring(0,leftIndex));// 从0到leftIndex拼接
                String key = sql.substring(leftIndex+2,rightIndex);// 找到其中的key取出、
                newSql.append("?");// 拼接问号
                keyList.add(key);//放进list集合中
            } else {
                newSql.append(sql);
                break;
            }
            sql = sql.substring(rightIndex+1);
        }
//        System.out.println(newSql);
//        System.out.println(keyList);
        // 解析得到两个值 怎么把这两个值返回 可以用map 可以用 对象
        return new SqlAndKey(newSql.toString(),keyList);
    }


    // 写一个方法来处理参数和sql问号中的值 参数中的值与问号对应
    // 需要obj statement keyList
    private void handlerParam(Object obj ,PreparedStatement statement , List<String> keyList) throws SQLException {
        // 1.首先判断传递的参数属于哪一类(通过反射)
        // 可能是int float 等基本数据类型 statement.setInt()...
        // 可能是String statement.setString();
        // 可能是一个对象
        // 可能是一个map集合
        Class clazz = obj.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            statement.setInt(1, (Integer) obj);
        } else if (clazz == float.class || clazz == Float.class) {
            statement.setFloat(1, (Float) obj);
        } else if (clazz == String.class) {
            statement.setString(1, (String) obj);
        } else {
            // 引用数据类型
            // 可能是对象或者map
            if (obj instanceof Map) {
                // 是个map
                handlerMap(obj,statement,keyList);
            } else {
                // 是个对象
                handlerDomain(obj,statement,keyList);
            }
        }
    }

    // 设计一个方法 处理map中的参数
    private void handlerMap(Object obj ,PreparedStatement statement,List<String> keyList) throws SQLException {
        // 把obj强转为map集合
        Map paramMap = (Map) obj;
        // 遍历集合 从keyList中取到key 从map中取到值 然后给sql中的?赋值
        for (int i = 0 ; i < keyList.size() ;i++) {
            String key = keyList.get(i);
            Object param = paramMap.get(key);
            statement.setObject(i+1,param);
        }
    }

    // 设计一个方法 处理domain
    private void handlerDomain(Object obj ,PreparedStatement statement ,List<String> keyList) throws SQLException {
        // 这是一个实体对象
        // 要找到实体对象中和key对应的的私有属性
        // 再通过属性名字找到get方法
        // 执行得到值
        // 赋值到sql的?上
        Class clazz = obj.getClass();
        // 遍历keyList
        for (int i = 0 ; i < keyList.size() ; i++) {
            String key = keyList.get(i);
            try {
                Field field = clazz.getDeclaredField(key);
                String fieldName = field.getName();
                // 通过字符串拼接得到get方法名字
                String methodName = "get" + fieldName.substring(0,1).toUpperCase() +fieldName.substring(1);
                Method method = clazz.getDeclaredMethod(methodName);
                Object param = method.invoke(obj);
                statement.setObject(i+1,param);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }



    // 封装了      1.解析sql
    //             2.获取连接池、获取连接
    //             3.获取状态参数
    PreparedStatement handlerSqlAndParam(Connection connection ,String sql ,Object obj ,PreparedStatement statement) throws SQLException {
        // 1.解析sql得到sql和key的对象
        SqlAndKey sqlAndKey= this.parseSql(sql);
        // 2.获取连接池、获取连接
        connection = DbPool.getDbPool().getConnection();
        statement = connection.prepareStatement(sqlAndKey.getSql());
        // 3.给sql中的? 赋值
        // 处理参数：statement obj
        if(obj != null) {
            this.handlerParam(obj, statement, sqlAndKey.getKeyList());
        }
        return statement;
    }

    // 设计一个处理ResultSet的方法
    // 得到的结果可能要组装成int float String  map 或者对象、
    // 怎么确定组成什么类型？ 人为给定一个类型 然后再根据给定的类型去组装
    // 参数 ： ResultSet 组装成的类型
    <T>T handlerResult(ResultSet resultSet ,Class resultType) throws SQLException {
        Object result = null;
        if (resultType == int.class || resultType == Integer.class) {
            result = resultSet.getInt(1);
        } else if (resultType == float.class || resultType == Float.class) {
            result = resultSet.getFloat(1);
        } else if (resultType == String.class) {
            result = resultSet.getString(1);
        } else {
            try {
                // resultType 为对象 或者为map集合
                // 先创键对象 再判断是否为map 否则为domain
                Object obj = resultType.newInstance();
                if (obj instanceof HashMap) {
                    this.getMap(resultSet ,(HashMap) obj);
                } else {
                    //为对象
                    getDomian(resultSet,obj);
                }
                result = obj;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) result;
    }

    // 给定的返回值结果是一个map集合
    // 先得到所有的字段名字
    // 然后根据字段名字取得值
    // 然后撞到map集合中
    private void getMap(ResultSet resultSet ,Map map) throws SQLException {
        // 1.得到所有的字段名字
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 2.遍历字段名字
        for (int i = 0 ; i < metaData.getColumnCount() ; i++) {
            String columnName = metaData.getColumnName(i+1);
            Object value = resultSet.getObject(columnName);
            // 存到map集合中
            map.put(columnName,value);
        }
    }

    // 给定的返回值是一个实体对象
    // 先找到所有的字段名字 （规定字段名字和属性名字一样）
    // 遍历字段名字
    // 通过字段名字找到对应的实体对象的属性
    // 根据属性名字找到对应的set方法
    // 给属性赋值
    private void getDomian(ResultSet resultSet ,Object obj) throws SQLException {
        Class clazz = obj.getClass();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0 ; i <metaData.getColumnCount() ; i++) {
            String columnName = metaData.getColumnName(i+1);
            Object value = resultSet.getObject(columnName);
            try {
                // 通过反射找到属性对应的set方法
                Field field = clazz.getDeclaredField(columnName);
                String fieldName = field.getName();
                String methodName = "set" +fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                Method method = clazz.getDeclaredMethod(methodName,field.getType());
                // 执行方法  参数为value
                method.invoke(obj,value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
