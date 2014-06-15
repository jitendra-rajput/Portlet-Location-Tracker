package com.liferay.locationTracker.portlet.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.PortletTitleComparator;

public class LocationTrackerUtil
{

	
	/**
	 * Get All portlets for Current Company
	 * @param companyId
	 * @param locale
	 * @return
	 */
    public static List<Portlet> getPortletList(long companyId , Locale locale)
    {
        List<Portlet> portletList = null;
        try
        {
            portletList = PortletLocalServiceUtil.getPortlets(companyId, false, false);
            portletList = ListUtil.sort(portletList, new PortletTitleComparator(locale));
           
        } catch (SystemException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return portletList;
    }

    /**
     * Get Portlet based on portlet id
     * @param portletId
     * @return
     */
    public static Portlet getPortlet(String portletId)
    {

        return PortletLocalServiceUtil.getPortletById(portletId);
    }

    /**
     * Get Portlet locations
     * @param groupList
     * @param privateLayout
     * @param portletId
     * @return
     */
    public static List<Layout> doGetPortletLocation(List<Group> groupList, boolean privateLayout,
            String portletId)
    {
        List<Layout> portletDetailsList = new ArrayList<Layout>();

        if (Validator.isNotNull(groupList) && !groupList.isEmpty())
        {

            for (Group group : groupList)
            {
                long scopeGroupId = group.getGroupId();
                long groupId = getGroupId(group);
                try
                {
                    List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, privateLayout,
                            LayoutConstants.TYPE_PORTLET);

                    for (Layout layout : layouts)
                    {
                        LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();

                        if (layoutTypePortlet.hasPortletId(portletId))
                        {
                            if (PortalUtil.getScopeGroupId(layout, portletId) == scopeGroupId)
                            {
                               
                                portletDetailsList.add(layout);
                            }
                        }
                    }
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        return portletDetailsList;
    }

    /**
     * Get All Groups for Current Company
     * @param companyId
     * @return
     */
    public static List<Group> getCompanyGroups(long companyId)
    {

        List<Group> groupList = new ArrayList<Group>();
            try
            {
                groupList = GroupLocalServiceUtil.getCompanyGroups(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
            } catch (SystemException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        return groupList;
    }
    
    /**
     * Get Group Id
     * @param group
     * @return
     */
    public static long getGroupId(Group group)
    {

        long groupId = group.getGroupId();
        try
        {

            if (group.isLayout())
            {
                Layout scopeLayout = LayoutLocalServiceUtil.getLayout(group.getClassPK());

                groupId = scopeLayout.getGroupId();
            }
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage());
        }
        return groupId;

    }
    
    /**
     * Get Portlet Title
     * @param portlet
     * @param locale
     * @param request
     * @return
     */
    public static String getPortletTitle(Portlet portlet , Locale locale , HttpServletRequest request){
        
        String portletTile = PortalUtil.getPortletTitle(portlet,request.getSession().getServletContext(),locale);
        return portletTile;
    }
    
    
    /**
     * Get locations for selected portlet
     * @param portletRequest
     * @return
     */
    public static List<Layout> findPortlet(PortletRequest portletRequest)
    {
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        List<Layout> layoutList = new ArrayList<Layout>();
        String portletId = ParamUtil.getString(portletRequest, "portletId");
        LOGGER.error("portlet id--" + portletId);
        if(Validator.isNotNull(portletId)){
        Portlet portlet = LocationTrackerUtil.getPortlet(portletId);
        List<Group> groupList = LocationTrackerUtil.getCompanyGroups(themeDisplay.getCompanyId());
        List<Layout> publiclayoutList = doGetPortletLocation(groupList, false, portlet.getPortletId());
        List<Layout> privateLayoutList = doGetPortletLocation(groupList, true, portlet.getPortletId());
        if(Validator.isNotNull(publiclayoutList) && !publiclayoutList.isEmpty()){
        	layoutList.addAll(publiclayoutList);
        }
        if(Validator.isNotNull(privateLayoutList) && !privateLayoutList.isEmpty()){
        	layoutList.addAll(privateLayoutList);
        }
        SessionMessages.add(portletRequest, "your-request-completed-successfully");
       
        }
        return layoutList;
    }

    
    
    /**
     * Get Search Container Object
     * @param renderRequest
     * @param renderResponse
     * @return
     */
    public static SearchContainer<Layout> getSearchContainer(RenderRequest renderRequest ,RenderResponse renderResponse){
    	
    	SearchContainer<Layout> searchContainer = null;
    	List<Layout> layoutList = findPortlet(renderRequest);
    	PortletURL portletURL = renderResponse.createRenderURL();
    	String portletId = ParamUtil.getString(renderRequest, "portletId");
    	portletURL.setParameter("portletId", portletId);

    	searchContainer = new SearchContainer<Layout>(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, null, "no-locations-were-found");
    	
		if (Validator.isNotNull(layoutList)) {
			List<Layout> results = ListUtil.subList(layoutList,
					searchContainer.getStart(), searchContainer.getEnd());
			searchContainer.setResults(results);
			searchContainer.setTotal(layoutList.size());
		}
		return searchContainer;
    	
    }
    
    /**
     * Get Page URL where portlet is placed
     * @param isPrivateLayout
     * @param friendlyURL
     * @param groupFriendlyURL
     * @param themeDisplay
     * @return
     */
    public static String getPageURL(boolean isPrivateLayout, String friendlyURL,String groupFriendlyURL , ThemeDisplay themeDisplay)
    {
        StringBuilder sb = new StringBuilder();

           //String portalURL = PortalUtil.getPortalURL(company.getVirtualHostname(), PortalUtil.getPortalPort(false), false);
           sb.append(themeDisplay.getPortalURL());
            if (isPrivateLayout)
            {
                sb.append(PortalUtil.getPathFriendlyURLPrivateGroup());
            } else
            {
                sb.append(PortalUtil.getPathFriendlyURLPublic());
            }

            sb.append(groupFriendlyURL);
            sb.append(friendlyURL);
        return sb.toString();
    }
    
    
	public static String getGroupDescriptiveName(Layout layout) {

		String groupName = StringPool.BLANK;
		if (Validator.isNotNull(layout)) {

			try {
				groupName = layout.getGroup().getDescriptiveName();
			} catch (PortalException e) {

				LOGGER.error(e.getMessage(), e);
			} catch (SystemException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return groupName;
	}
    
   
  
    

	private static final Log LOGGER = LogFactoryUtil.getLog(LocationTrackerUtil.class);
}
