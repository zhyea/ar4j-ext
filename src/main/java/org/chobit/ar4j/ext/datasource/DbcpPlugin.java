package org.chobit.ar4j.ext.datasource;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.chobit.ar4j.core.datasource.DataSourcePlugin;
import org.chobit.ar4j.core.exception.ArConfigException;

import javax.sql.DataSource;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.chobit.ar4j.core.tools.Strings.isBlank;
import static org.chobit.ar4j.core.tools.Strings.isNotBlank;

public class DbcpPlugin implements DataSourcePlugin {

    private final String url;
    private final Properties props = new Properties();

    private DataSource dataSource;

    public DbcpPlugin(String url, String username, String password) {
        this(url, username, password, new Properties());
    }

    public DbcpPlugin(String url, Properties props) {
        this(url, null, null, props);
    }

    public DbcpPlugin(String url, String username, String password, Properties props) {
        if (isBlank(url)) {
            throw new ArConfigException("The url for datasource is empty.");
        }
        addDefaultProps();
        this.url = url;
        if (null != props) {
            this.props.putAll(props);
        }
        if (isNotBlank(username)) {
            this.props.put("user", username);
        }
        if (isNotBlank(password)) {
            this.props.put("password", password);
        }
        this.dataSource = buildDataSource();
    }


    @Override
    public DataSource getDataSource() {
        if (null == this.dataSource) {
            this.dataSource = buildDataSource();
        }
        return this.dataSource;
    }


    private void addDefaultProps() {
        this.props.put("testOnBorrow", true);
        this.props.put("testWhileIdle", true);
        this.props.put("testOnReturn", true);
        this.props.put("validationQuery", "SELECT 1");
        this.props.put("timeBetweenEvictionRunsMillis", MINUTES.toMillis(3));
        this.props.put("minEvictableIdleTimeMillis", MINUTES.toMillis(10));
    }

    private DataSource buildDataSource() {
        try {
            Class.forName(Driver.class.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(this.url, this.props);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        GenericObjectPoolConfig<PoolableConnection> poolConfig = new GenericObjectPoolConfig<PoolableConnection>();
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(poolableConnectionFactory, poolConfig);
        poolableConnectionFactory.setPool(connectionPool);
        PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);
        return dataSource;
    }
}
