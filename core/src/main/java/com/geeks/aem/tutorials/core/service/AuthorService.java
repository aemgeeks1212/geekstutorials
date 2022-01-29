package com.geeks.aem.tutorials.core.service;

import org.apache.sling.api.SlingHttpServletRequest;

public interface AuthorService {
    public String createAuthorNode(String country, SlingHttpServletRequest request);
}
