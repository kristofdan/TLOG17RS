package com.kristofdan.tlog16rs.core.beans;

import com.avaje.ebean.*;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.kristofdan.tlog16rs.entities.TestEntity;

/**
 *
 * @author kristof
 */

public class CreateDataBase {
    DataSourceConfig dataSourceConfig;
    ServerConfig serverConfig;
    EbeanServer ebeanServer;

    public CreateDataBase(){
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
        serverConfig.setName("timelogger");
        serverConfig.setDdlGenerate(true);
        serverConfig.setDdlRun(true);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);
        serverConfig.setDefaultServer(true);
    }
}
