<%@page import="com.liferay.locationTracker.portlet.util.LocationTrackerUtil"%>
<%
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@page import="com.liferay.portal.model.Portlet"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:renderURL var="findPortletURL"></portlet:renderURL> 
<portlet:resourceURL  var="exportToExcelURL"/>
<script type="text/javascript">

function findPortlets(){
	document.getElementById('portletSearchForm').action='<%= findPortletURL %>';
	document.getElementById('portletSearchForm').submit();
}
function exportToExcel(url){
	
	document.getElementById('portletSearchForm').action=url + "&portletId=" + document.getElementById('<portlet:namespace />portletId').value;
	document.getElementById('portletSearchForm').submit();
}



</script>


<liferay-ui:success key="your-request-completed-successfully" message="your-request-completed-successfully"></liferay-ui:success> 
<form name="portletSearchForm" id="portletSearchForm" method="post">

<div>
<table>

<tr>
<td> Portlet :</td>
<td> 
<select name="<portlet:namespace />portletId" id="<portlet:namespace />portletId">
			
			<option value="">Select</option>
			 <c:forEach var="portlet" items="${portletList}">
			 
			   <option <c:if test="${selectedPortletId == portlet.portletId}">selected="selected"</c:if>  value="${portlet.portletId}">
			   <%= PortalUtil.getPortletTitle((Portlet)pageContext.getAttribute("portlet"), application,locale)%></option>
			 
			 </c:forEach>
						
</select>
</td>
<td><input type="button" value="Find" onclick="javascript:findPortlets()" class="btn btn-primary"></input></td>
</tr>

</table>
</div>

<br/>


<liferay-ui:search-container hover="false"  searchContainer="${searchContainer}">

	<liferay-ui:search-container-results 
		results="${searchContainer.results}"
		total="${searchContainer.total}" />
	<liferay-ui:search-container-row 
		className="com.liferay.portal.model.Layout"
		keyProperty="layoutId" modelVar="layoutObj">
		
		<liferay-ui:search-container-column-text name="page-name" property="name"/>
		<liferay-ui:search-container-column-text name="group" value="<%= layoutObj.getGroup().getDescriptiveName() %>">
		
			
		</liferay-ui:search-container-column-text>
		<liferay-ui:search-container-column-text name="friendly-url" property="friendlyURL"/>
		<liferay-ui:search-container-column-text name="is-private-page" property="privateLayout"/>
		<liferay-ui:search-container-column-text name="page-url" buffer="bufferSelection">
		
		<%
			bufferSelection.append("<a target='_blank' href='");
			bufferSelection.append(LocationTrackerUtil.getPageURL(layoutObj.isPrivateLayout(), layoutObj.getFriendlyURL(), layoutObj.getGroup().getFriendlyURL(), themeDisplay));
			bufferSelection.append("'>Go to Page</a>");
		%>
		
		
		</liferay-ui:search-container-column-text>
		
		
	</liferay-ui:search-container-row>
	<liferay-ui:search-iterator/>
</liferay-ui:search-container>

<c:if test="${not empty searchContainer.results}">

<div>
	<input type="button" value="Export To Excel" onclick="javascript:exportToExcel('<%= exportToExcelURL %>')" class="btn btn-primary"></input>
</div>

</c:if>


</form>