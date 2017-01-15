package com.zhyea.ar4j.ext.datasource;

import com.zhyea.ar4j.core.datasource.DataSourcePlugin;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.sql.DataSource;

public class DbcpPlugin implements DataSourcePlugin {

    private final String url;
    private final String username;
    private final String password;

    private DataSource dataSource;

    public DbcpPlugin(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dataSource = buildDataSource();
    }


    @Override
    public DataSource getDataSource() {
        if (null == this.dataSource) {
            this.dataSource = buildDataSource();
        }
        return this.dataSource;
    }


    private DataSource buildDataSource() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(this.url, this.username, this.password);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory, poolConfig);
        poolableConnectionFactory.setPool(connectionPool);
        PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);
        return dataSource;
    }
}
