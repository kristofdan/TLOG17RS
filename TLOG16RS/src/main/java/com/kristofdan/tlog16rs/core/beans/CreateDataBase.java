package com.kristofdan.tlog16rs.core.beans;

import com.avaje.ebean.*;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.kristofdan.tlog16rs.TLOG16RSConfiguration;
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

    public CreateDataBase(TLOG16RSConfiguration config){
        try {
            updateSchema(config);
        } catch (Exception e) {
            log.error("Error in method updateSchema", e);
        }
        createDataSourceConfig(config);
        createServerConfig(config);
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    
    private void createDataSourceConfig(TLOG16RSConfiguration config){
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDriver());
        dataSourceConfig.setUrl(config.getUrl());
        dataSourceConfig.setUsername(config.getUsername());
        dataSourceConfig.setPassword(config.getPassword());
    }
    
    private void createServerConfig(TLOG16RSConfiguration config){
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getConfigname());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);
        serverConfig.setDefaultServer(true);
    }
    
    private void updateSchema(TLOG16RSConfiguration config)
        throws Exception
    {
        Class.forName(config.getDriver());               //Loading the driver
        Connection connection = DriverManager.getConnection(
                (config.getUrl()),
                (config.getUsername()),
                (config.getPassword()));
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(
                "/home/kristof/Precognox/POLC/REST service with Dropwizard/TLOG16RS/src/main/resources/migrations.xml",
                new FileSystemResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}