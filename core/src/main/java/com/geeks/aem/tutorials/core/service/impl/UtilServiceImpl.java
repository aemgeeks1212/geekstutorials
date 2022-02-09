package com.geeks.aem.tutorials.core.service.impl;

import com.geeks.aem.tutorials.core.service.UtilService;
import com.geeks.aem.tutorials.core.utils.ResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = UtilService.class,
        immediate = true,
        name = "UtilService"
)
public class UtilServiceImpl implements UtilService {
    private static final Logger LOG = LoggerFactory.getLogger(UtilServiceImpl.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    SlingSettingsService slingSettingsService;

    @Override
    public String getActionURL(Resource resource) {
        String actionURL= StringUtils.EMPTY;
        try {
            ResourceResolver resourceResolver=ResolverUtil.newResolver(resourceResolverFactory);
            String actionType=resource.getValueMap().get("actionType",String.class);
            String selector=resourceResolver.getResource(actionType).getValueMap().get("selector",String.class);
            actionURL=resource.getPath()+"."+selector+".json";
        }catch (Exception e){
            LOG.info("\n Error while getting Action URL - {} ",e.getMessage());
        }

        return actionURL;
    }

    @Override
    public boolean isPublish(){
        return slingSettingsService.getRunModes().contains("publish");
    }

    @Override
    public boolean isAuthor(){
        return slingSettingsService.getRunModes().contains("author");
    }
}
