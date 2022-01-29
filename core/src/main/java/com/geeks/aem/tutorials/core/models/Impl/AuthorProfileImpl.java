package com.geeks.aem.tutorials.core.models.Impl;

import com.day.cq.wcm.api.Page;
import com.geeks.aem.tutorials.core.models.AuthorProfile;
import com.geeks.aem.tutorials.core.models.AuthorsInfo;
import com.geeks.aem.tutorials.core.service.AuthorService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = AuthorProfile.class,
        resourceType = AuthorProfileImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class AuthorProfileImpl implements AuthorProfile {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorProfileImpl.class);
    final protected static String RESOURCE_TYPE="geekstutorials/components/custom/authorsinfo";

    @SlingObject
    ResourceResolver resourceResolver;

    @ScriptVariable
    Page currentPage;

    @OSGiService
    AuthorService authorService;

    @SlingObject
    SlingHttpServletRequest request;

    String firstName;
    String lastName;
    String email;
    String phone;
    List<String> books;


    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public List<String> getBooks() {
        return books;
    }

    @PostConstruct
    protected void init(){
/*        LOG.info("\n ----------------IN MODEL----------------");
        String country=currentPage.getPath().split("/")[3];
        String authorPath=request.getRequestParameter("author").getString();
        Resource author=authorService.getAuthorDetails(country,authorPath);
        ValueMap authorMap=author.getValueMap();
        firstName=authorMap.get("fname",String.class);
        lastName=authorMap.get("lname",String.class);
        email=authorMap.get("email",String.class);
        phone=authorMap.get("phone",String.class);
        books= Arrays.asList(authorMap.get("books",String[].class));
        LOG.info("\n Resource Path - {} : {} ",author.getPath(),books.size());*/


    }
}
