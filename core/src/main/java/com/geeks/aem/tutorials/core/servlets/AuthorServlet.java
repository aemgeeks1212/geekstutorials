package com.geeks.aem.tutorials.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.geeks.aem.tutorials.core.constants.Constants;
import com.geeks.aem.tutorials.core.service.AuthorService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        methods = {HttpConstants.METHOD_POST},
        resourceTypes = Constants.ADDAUTHOR_RESOURCE_TYPE,
        selectors = {Constants.ADDAUTHOR_SELECTORS},
        extensions = {"json"}
)
public class AuthorServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorServlet.class);

    @Reference
    AuthorService authorService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            LOG.info("\n -------STARTED POST---------");
           List<RequestParameter> requestParameterList=request.getRequestParameterList();
            for(RequestParameter requestParameter : requestParameterList){
                if(requestParameter.isFormField()) {
                    LOG.info("\n ==PARAMETERS FIELD===>  {} : {} ", requestParameter.getName(), requestParameter.getString());
                }else{
                    LOG.info("\n ==PARAMETERS ELSE===>  {}  ", requestParameter.getName(), requestParameter.getString());
                }
            }

            /*Map<String,RequestParameter[]> params=request.getRequestParameterMap();
            for(Map.Entry<String,RequestParameter[]> pair:params.entrySet()){
                RequestParameter[] prr=pair.getValue();
                for(RequestParameter rp:prr){
                    LOG.info("\n ==PARAMETERS MAP===>  {} : {} : {}",rp.getName(),rp.getString(),rp.isFormField());
                }

            }*/
            boolean isMultipart=ServletFileUpload.isMultipartContent(request);
            String resp=authorService.createAuthorNode(getCountry(request),request);
            response.getWriter().write(resp);
        }
        catch (Exception e){
            LOG.info("\n ERROR IN REQUEST {} ",e.getMessage());
        }
        //response.getWriter().write("=FORM SUBMITTED=");
    }

    public String getCountry(SlingHttpServletRequest request){
        String country=request.getResource().getPath().split("/")[3];
        LOG.info("\n --COUNTRY-- {} ",country);
        return country;
    }

}
