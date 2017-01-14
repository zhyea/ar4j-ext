package com.zhyea.ar4j.ext.dialect;


import com.zhyea.ar4j.core.dialect.Dialect;

public class MySqlDialect implements Dialect {

    @Override
    public String defaultPrimaryKey() {
        return "id";
    }

    @Override
    public String sqlShowColumns() {
        return "select * from ${TABLE_NAME} limit 0";
    }

    @Override
    public String sqlShowTables() {
        return "show tables like '${TABLE_NAME}%'";
    }
}
