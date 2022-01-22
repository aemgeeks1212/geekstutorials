package com.geeks.aem.tutorials.core.service.impl;

import com.geeks.aem.tutorials.core.constants.Constants;
import com.geeks.aem.tutorials.core.service.AuthorService;
import com.geeks.aem.tutorials.core.service.AuthorServiceConfig;
import com.geeks.aem.tutorials.core.utils.ResolverUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import java.io.InputStream;
import java.util.*;

@Component(
        service = AuthorService.class,
        name = "AuthorService",
        immediate = true
)
public class AuthorServiceImpl implements AuthorService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorServiceImpl.class);

    @Reference
    AuthorServiceConfig authorServiceConfig;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public String createAuthorNode(String country, SlingHttpServletRequest request) {
        LOG.info("\n --------------CREATE AUTHOR METHOD----------- {} ",country);
       String nodeCreated= StringUtils.EMPTY;
       try {
           //LOG.info("\n --------CALLING SERVICE-------- {} ",country);
           AuthorServiceConfig config = authorServiceConfig.getCountryConfig(country);
           LOG.info("\n --------CONFIG---------- {} ",config.getCountryCode());
           String nodeLocation = config.getNodePath() + "/" + config.getNodeName();
           //LOG.info("\n -------NODE LOCATION--------- {} ",nodeLocation);
           ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
           Session session=resourceResolver.adaptTo(Session.class);
           if(session.nodeExists(nodeLocation)){
               LOG.info("\n ----INSIDE IF-----------");
               Node parentNode = session.getNode(nodeLocation);
               if(!parentNode.hasNode(getNodeName(request))) {
                   Node authorNode = parentNode.addNode(getNodeName(request), Constants.AUTHORNODE_TYPE);
                   authorNode.setProperty("fname", getRequestParamter(request, "fname"));
                   authorNode.setProperty("lname", getRequestParamter(request, "lname"));
                   authorNode.setProperty("email", getRequestParamter(request, "email"));
                   authorNode.setProperty("phone", getRequestParamter(request, "phone"));
                   authorNode.setProperty("books", request.getParameter("books").split(","));
                   addThumbnail(authorNode,request);
                   session.save();
                   nodeCreated = authorNode.getName() + " added.";
               }else {
                   nodeCreated = "This author already exists.";
               }
           }else{
               LOG.info("\n ----INSIDE ELSE-------{} ",config.getNodePath());
               if(session.nodeExists(config.getNodePath())){
                   //LOG.info("\n ----INSIDE ELSE IF-------{} ",config.getNodePath());
                   Node gParentNode=session.getNode(config.getNodePath());
                   //LOG.info("\n ----Parent Node-------{} ",gParentNode.getPath());
                   Node parentNode=gParentNode.addNode(config.getNodeName(),Constants.AUTHORNODE_TYPE);
                   session.save();
                   nodeCreated=parentNode.getName()+" added.";
               }
           }
       }catch (Exception e){
           LOG.error("\n Error while creating node - {} ",e.getMessage());
       }
        return nodeCreated;
    }

    @Override
    public List<Map<String, String>> getAuthors(final String country) {
        LOG.info("\n -------- GET COMPONENT METHOD----------- ");
        // local variables
        final List<Map<String, String>> authorList = new ArrayList<Map<String, String>>();
        AuthorServiceConfig config = authorServiceConfig.getCountryConfig(country);
        LOG.info("\n --------CONFIG---------- {} ",config.getCountryCode());
        String nodeLocation = config.getNodePath() + "/" + config.getNodeName();
        try {
            LOG.info("\n ---resolver not found---> ");
            ResourceResolver resolverResolver = ResolverUtil.newResolver(resourceResolverFactory);
            LOG.info("\n ---resolver HIT ---> " + resolverResolver.getUserID());
            //final Session session = resolver.adaptTo(Session.class);
            Iterator<Resource> authors=resolverResolver.getResource(nodeLocation).listChildren();
            while (authors.hasNext()){
                Resource resource=authors.next();
                Map<String,String> author=new HashMap<>();
                ValueMap prop=resource.getValueMap();
                author.put("fname",getProprty(prop,"fname"));
                author.put("lname",getProprty(prop,"lname"));
                author.put("email",getProprty(prop,"email"));
                author.put("phone",getProprty(prop,"phone"));
                author.put("books",Arrays.toString(prop.get("books",String[].class)));
                //author.put("books", ArrayUtils.toString(prop.get("books",String.class)));
                author.put("booksCount",Integer.toString(prop.get("books",String[].class).length));
                author.put("image", resource.getPath()+"/photo/image");
                //LOG.info("\n {} - {} - {}",author.get("fname"),author.get("lname"), Arrays.toString(author.get("books")));
                authorList.add(author);
            }
        } catch (Exception e) {
            LOG.error("Occurred exception - {}", e.getMessage());
        }

        return authorList;
    }

    @Override
    public Resource getAuthorDetails(final String country,final String author) {
        AuthorServiceConfig config = authorServiceConfig.getCountryConfig(country);
        String nodeLocation = config.getNodePath() + "/" + config.getNodeName();
        try {
            ResourceResolver resolverResolver = ResolverUtil.newResolver(resourceResolverFactory);
            LOG.info("\n ---resolver HIT ---> " + resolverResolver.getUserID());
            Resource authorDetails=resolverResolver.getResource(nodeLocation+"/"+author);
            return authorDetails;

        } catch (Exception e) {
            LOG.error("Occurred exception - {}", e.getMessage());
        }

        return null;
    }

    private String getNodeName(SlingHttpServletRequest request){
        String fName=request.getParameter("fname");
        String lName=request.getParameter("lname");
        String email=request.getParameter("email");
        String[] books=request.getParameter("books").split(",");
        for(String book:books){
            //LOG.info("\n B - {} ",book);
        }
        String authorNodeName=fName+"-"+lName+"-"+email;
        return authorNodeName;
    }

    private String getRequestParamter(SlingHttpServletRequest request,String name){
        String requestParameter=request.getParameter(name);
        return requestParameter;
    }

    private String getProprty(ValueMap valueMap,String prop){

        if(StringUtils.isNotBlank(valueMap.get(prop,String.class))){
            return valueMap.get(prop,String.class);
        }
        return "NA";
    }

    private boolean addThumbnail(Node node,SlingHttpServletRequest request){
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            RequestParameter rp = request.getRequestParameter("file");
            LOG.info("\n FILE INFO -- {} : {} : {} ", rp.getFileName(), rp.getContentType(), rp.isFormField());
            InputStream is = rp.getInputStream();
            Session session=resourceResolver.adaptTo(Session.class);
            ValueFactory valueFactory=session.getValueFactory();
            Binary imageBinary=valueFactory.createBinary(is);

            Node photo=node.addNode("photo","sling:Folder");
            Node file=photo.addNode("image","nt:file");
            Node content = file.addNode("jcr:content", "nt:resource");
            content.setProperty("jcr:mimeType", rp.getContentType());
            content.setProperty("jcr:data", imageBinary);

            return true;

        }catch (Exception e){
            LOG.info("\n ERROR - {} ",e.getMessage());
        }
        return false;
    }
}
