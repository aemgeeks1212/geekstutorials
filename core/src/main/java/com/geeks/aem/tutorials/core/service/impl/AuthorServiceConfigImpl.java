package com.geeks.aem.tutorials.core.service.impl;

import com.geeks.aem.tutorials.core.config.CountriesConfig;
import com.geeks.aem.tutorials.core.service.AuthorServiceConfig;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;


@Component (service = AuthorServiceConfig.class,configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate (ocd = CountriesConfig.class, factory = true)
public class AuthorServiceConfigImpl implements AuthorServiceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorServiceConfigImpl.class);


    private String countryCode;
    private String jsonName;
    private String jsonPath;
    private String authorsNodeName;
    private String authorsNodePath;
    private List<AuthorServiceConfig> configs;

    @Activate
    @Modified
    protected void activate(final CountriesConfig config) {
        countryCode = config.countryCode();
        jsonName = config.jsonName();
        jsonPath = config.jsonPath();
        authorsNodeName = config.authorsNode();
        authorsNodePath = config.authorsPath();
    }

    @Reference(service = AuthorServiceConfig.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindAuthorServiceConfig(final AuthorServiceConfig config) {
        if (configs == null) {
            configs = new ArrayList<>();
        }
        configs.add(config);

    }

    public void unbindAuthorServiceConfig(final AuthorServiceConfig config) {
        configs.remove(config);
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getJsonName() {
        return jsonName;
    }

    @Override
    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public String getNodeName() {
        return authorsNodeName;
    }

    @Override
    public String getNodePath() {
        return authorsNodePath;
    }

    @Override
    public List<AuthorServiceConfig> getAllConfigs() {
        return configs;
    }

    @Override
    public AuthorServiceConfig getCountryConfig(String countryCode) {
        for (AuthorServiceConfig confFact : configs) {
            if (StringUtils.equalsIgnoreCase(countryCode, confFact.getCountryCode())) {
                LOG.info("\n CONFIG SERVICE - {} ",confFact.getCountryCode());
                return confFact;
            }
        }
        return null;
    }
}
