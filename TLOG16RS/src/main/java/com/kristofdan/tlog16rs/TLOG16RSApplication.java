package com.kristofdan.tlog16rs;

import com.kristofdan.tlog16rs.core.beans.CreateDataBase;
import com.kristofdan.tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TLOG16RSApplication extends Application<TLOG16RSConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TLOG16RSApplication().run(args);
    }

    @Override
    public String getName() {
        return "TLOG16RS";
    }

    @Override
    public void initialize(final Bootstrap<TLOG16RSConfiguration> bootstrap) {
        //Initialization not needed
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) {
        CreateDataBase createDataBase = new CreateDataBase(configuration);
        environment.jersey().register(new TLOG16RSResource());
    }
}
