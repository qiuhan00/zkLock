package com.cfang.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DataSourceConfigurations {

	@Value("${driverClass:com.mysql.jdbc.Driver}")
    private String driverClass;
	@Value("${db.jdbcUrl}")
	private String jdbcUrl;
	@Value("${db.username}")
	private String username;
	@Value("${db.password}")
	private String password;
	@Value("${initialSize:10}")
    private Integer initialSize;	//初始化连接池个数
    @Value("${maxActive:20}")
    private Integer maxActive;	//最大连接数 
    @Value("${maxWait:60000}")
    private Long maxWait;	//获取连接等待超时时间，单位毫秒
    @Value("${timeBetweenEvictionRunsMillis:60000}")
    private Long timeBetweenEvictionRunsMillis;	//间隔多久进行一次检测，检测回收需要关闭的空闲链接，单位毫秒
    @Value("${minEvictableIdleTimeMillis:300000}")
    private Long minEvictableIdleTimeMillis;	//定义一个连接在池中的最小生存时间，单位毫秒
    @Value("${validationQuery:select 1 from dual}")
    private String validationQuery;	//用来检测连接是否有效的sql
    @Value("${testWhileIdle:true}")
    private Boolean testWhileIdle;	//申请连接的时候是否检测。如果空间时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
    @Value("${testOnBorrow:false}")
    private Boolean testOnBorrow;	
    @Value("${testOnReturn:false}")
    private Boolean testOnReturn;	//归还连接的时候，是否validationQuery检查连接的有效性，true执行的话，降低性能
    @Value("${poolPreparedStatements:false}")
    private Boolean poolPreparedStatements;	//是否打开pscache
    @Value("${maxPoolPreparedStatementPerConnectionSize:20}")
    private int maxPoolPreparedStatementPerConnectionSize;	//指定每个连接的pscache的大小
    
	@Bean
	public DataSource initDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setInitialSize(initialSize);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		if(poolPreparedStatements) {
			dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		}
		return dataSource;
	}
}
