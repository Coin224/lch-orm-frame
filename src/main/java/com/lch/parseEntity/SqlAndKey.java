package com.lch.parseEntity;

import java.util.List;

public class SqlAndKey {
    private String sql;
    private List<String> keyList;

    public SqlAndKey(String sql, List<String> keyList) {
        this.sql = sql;
        this.keyList = keyList;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getKeyList() {
        return keyList;
    }
}
