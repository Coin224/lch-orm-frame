package com.lch.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StatementAndConnection {


    private PreparedStatement statement;
    private Connection connection;

    public StatementAndConnection(PreparedStatement statement, Connection connection) {
        this.statement = statement;
        this.connection = connection;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public Connection getConnection() {
        return connection;
    }

}
