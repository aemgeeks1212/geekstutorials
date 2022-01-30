package com.geeks.aem.tutorials.core.service;

import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface AuthorService {
    public String createAuthorNode(String country, SlingHttpServletRequest request);
    public List<Map<String, String>> getAuthors(final String country);
}
