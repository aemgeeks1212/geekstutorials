<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'action' component

  Return the action path for the mail form handling

--%><%@page session="false" %><%
%><%@page import="com.day.cq.wcm.foundation.forms.FormsHelper"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%>
<%@page import="com.day.cq.wcm.foundation.forms.FormsConstants"%><sling:defineObjects/><%
    FormsHelper.setForwardPath(slingRequest, resource.getPath() + ".addauthor.json");
    FormsHelper.setRedirectToReferrer(request, true);
%>