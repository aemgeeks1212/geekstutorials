package com.geeks.aem.tutorials.core.schedulers;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.geeks.aem.tutorials.core.config.JSONCreatorConfig;
import com.geeks.aem.tutorials.core.constants.Constants;
import com.geeks.aem.tutorials.core.service.AuthorServiceConfig;
import com.geeks.aem.tutorials.core.service.UtilService;
import com.geeks.aem.tutorials.core.utils.ResolverUtil;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.json.JSONArray;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = JSONCreatorConfig.class)
public class JSONCreator implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(JSONCreator.class);

    @Reference
    ResourceResolverFactory resolverFactory;

    @Reference
    UtilService utilService;

    private int schedulerId;
    private String[] skipCountries;
    private String siteParent;
    private List<String> excludeSites;
    private List<AuthorServiceConfig> authorConfigs;

    @Reference
    private Scheduler scheduler;

    @Reference
    AuthorServiceConfig authorServiceConfig;

    @Activate
    protected void activate(JSONCreatorConfig config) {
        schedulerId = config.schedulerName().hashCode();
        skipCountries=config.getSkipSites();
        siteParent=config.getSiteParent();
        excludeSites=Arrays.stream(config.getSkipSites()).collect(Collectors.toList());
        authorConfigs=authorServiceConfig.getAllConfigs();
        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(JSONCreatorConfig config) {
        removeScheduler();
    }

    protected void removeScheduler() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    protected void addScheduler(JSONCreatorConfig config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
        scheduleOptions.name(String.valueOf(schedulerId));
        scheduler.schedule(this, scheduleOptions);
    }

    @Override
    public void run() {
        try {
            if(utilService.isPublish()){
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resolverFactory);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page = pageManager.getPage(siteParent);
            Iterator<Page> sites = page.listChildren();
            while (sites.hasNext()) {
                Page country = sites.next();
                Session session = resourceResolver.adaptTo(Session.class);

                JSONArray countryJson = new JSONArray();
                if (!excludeSites.contains(country.getName())) {
                    authorServiceConfig.getCountryConfig(country.getName());
                    Resource parentResource = resourceResolver.getResource(authorServiceConfig.getNodePath() + authorServiceConfig.getNodeName());
                    Iterator<Resource> authors = parentResource.listChildren();
                    while (authors.hasNext()) {
                        Resource author = authors.next();
                        ValueMap auhtorMap = author.getValueMap();
                        countryJson.put(auhtorMap);
                    }
                    String jsonPath = authorServiceConfig.getJsonPath() + authorServiceConfig.getJsonName();
                    boolean jsonNodeExists = session.nodeExists(jsonPath);
                    ValueFactory valueFactory = session.getValueFactory();
                    InputStream inputStream = IOUtils.toInputStream(countryJson.toString(), StandardCharsets.UTF_8);
                    Binary contentValue = valueFactory.createBinary(inputStream);
                    Resource resource = resourceResolver.getResource(authorServiceConfig.getJsonPath());
                    Node node = resourceResolver.getResource(authorServiceConfig.getJsonPath()).adaptTo(Node.class);
                    if (!jsonNodeExists) {
                        try {
                            Node fileNode = node.addNode(authorServiceConfig.getJsonName(), Constants.NT_FILE);
                            fileNode.addMixin(Constants.MIX_REFERENCEABLE);
                            Node resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, Constants.NT_RESOURCE);
                            resNode.setProperty(Constants.JCR_MIME_TYPE, Constants.APPLICATION_JSON);
                            resNode.setProperty(Constants.JCR_DATA, contentValue);
                            Calendar lastModified = Calendar.getInstance();
                            lastModified.setTimeInMillis(lastModified.getTimeInMillis());
                            resNode.setProperty(Constants.JCR_MODIFIED, lastModified);
                            session.save();
                        } catch (Exception e) {
                            LOG.info("\n ERROR WHILE JSON CREATION {} ", e.getMessage());
                        }
                    }else{
                        Node jsonNode=session.getNode(jsonPath+"/"+JcrConstants.JCR_CONTENT);
                        jsonNode.setProperty(Constants.JCR_DATA, contentValue);
                        Calendar lastModified = Calendar.getInstance();
                        lastModified.setTimeInMillis(lastModified.getTimeInMillis());
                        jsonNode.setProperty(Constants.JCR_MODIFIED, lastModified);
                        session.save();
                        }
                    }
                }
            }
        }catch (Exception e){
            LOG.error("\n Error in scheduler {} ",e.getMessage());
        }
    }
}

