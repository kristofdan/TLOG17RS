package com.kristofdan.tlog16rs.core.beans;

import com.avaje.ebean.*;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.kristofdan.tlog16rs.entities.TestEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.*;
import liquibase.database.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author kristof
 */

@Slf4j
public class CreateDataBase {
    DataSourceConfig dataSourceConfig;
    ServerConfig serverConfig;
    EbeanServer ebeanServer;

    public CreateDataBase(){
        try {
            updateSchema();
        } catch (Exception e) {
            log.error("Error in method updateSchema", e);
        }
        createDataSourceConfig();
        createServerConfig();
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    
    private void createDataSourceConfig(){
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("org.mariadb.jdbc.Driver");
        dataSourceConfig.setUrl("jdbc:mariadb://127.0.0.1:9001/timelogger");
        dataSourceConfig.setUsername("timelogger");
        dataSourceConfig.setPassword("633Ym2aZ5b9Wtzh4EJc4pANx");
    }
    
    private void createServerConfig(){
        serverConfig = new ServerConfig();
        serverConfig.setName(System.getProperty("configname"));
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);
        serverConfig.setDefaultServer(true);
    }
    
    private void updateSchema()
        throws Exception
    {
        Class.forName(System.getProperty("driver"));               //Loading the driver
        Connection connection = DriverManager.getConnection(
                System.getProperty("url"),
                System.getProperty("username"),
                System.getProperty("password"));
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(
                "/home/kristof/Precognox/POLC/REST service with Dropwizard/TLOG16RS/src/main/resources/migrations.xml",
                new FileSystemResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
//jdbc:mysql://localhost:9001/?user=timelogger?password=633Ym2aZ5b9Wtzh4EJc4pANx
//jdbc:mariadb://localhost:9001/","timelogger","633Ym2aZ5b9Wtzh4EJc4pANx
