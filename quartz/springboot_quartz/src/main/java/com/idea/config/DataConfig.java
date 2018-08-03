package com.idea.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DataConfig {
	
	@Value("${jdbc.driver}")
	private String jdbcDriver;
	
	@Value("${jdbc.url}")
	private String jdbcURL;
	
	@Value("${jdbc.user}")
	private String jdbcUser;
	
	@Value("${jdbc.pass}")
	private String jdbcPass;

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getJdbcUser() {
		return jdbcUser;
	}

	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	public String getJdbcPass() {
		return jdbcPass;
	}

	public void setJdbcPass(String jdbcPass) {
		this.jdbcPass = jdbcPass;
	}
	
	@Bean
    @Primary
    public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(jdbcDriver);
		dataSource.setUrl(jdbcURL);
		dataSource.setUsername(jdbcUser);
		dataSource.setPassword(jdbcPass);
		dataSource.setInitialSize(2);
		dataSource.setMaxActive(10);
		dataSource.setMinIdle(1);
		//dataSource.addConnectionProperty("remarksReporting","true");
		dataSource.setMaxWait(60000);
		//dataSource.setValidationQuery("SELECT 1 from dual");
		dataSource.setTestOnBorrow(false);
		dataSource.setTestWhileIdle(false);
		dataSource.setPoolPreparedStatements(false);
		return dataSource;
    }
	
	 @Bean
	    public DataSourceTransactionManager transactionManager() {
	        return new DataSourceTransactionManager(dataSource());
	    }
	    
	    @Bean(name = "jdbcTemplate")
		public JdbcTemplate dumpJdbcTemplate(DataSource dataSource) {
		    return new JdbcTemplate(dataSource);
		}
}
