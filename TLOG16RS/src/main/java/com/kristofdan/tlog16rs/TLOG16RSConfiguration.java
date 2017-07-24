package com.kristofdan.tlog16rs;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
public class TLOG16RSConfiguration extends Configuration {
    
    @NotEmpty
    protected String driver;
    @NotEmpty
    protected String url;
    @NotEmpty
    protected String username;
    @NotEmpty
    protected String password;
    @NotEmpty
    protected String configname;
}
