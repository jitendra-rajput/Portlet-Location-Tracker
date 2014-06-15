package com.liferay.locationTracker.portlet.util;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import com.liferay.locationTracker.portlet.bean.LocationDetailsVO;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

public class LocationTrackerReportsUtil {

	
	/**
	 * Get Location detail list for Reports
	 * @param portletRequest
	 * @return
	 */
	public static List<LocationDetailsVO> getLocationDetailsVoList(PortletRequest portletRequest){
		
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<Layout> layoutList = LocationTrackerUtil.findPortlet(portletRequest);
		List<LocationDetailsVO> locationDetailsList = new ArrayList<LocationDetailsVO>();
		
		if(Validator.isNotNull(layoutList) && !layoutList.isEmpty()){
			
			for(Layout layout : layoutList){
				
				LocationDetailsVO locationVo = new LocationDetailsVO();
				locationVo.setName(layout.getName(themeDisplay.getLocale()));
				LOGGER.debug("Name-" + layout.getName());
				locationVo.setGroup(LocationTrackerUtil.getGroupDescriptiveName(layout));
				locationVo.setPrivateLayout(layout.isPrivateLayout());
				locationVo.setFriendlyURL(layout.getFriendlyURL());
				locationVo.setPageUrl(LocationTrackerUtil.getPageURL(layout.isPrivateLayout(), layout.getFriendlyURL(), getGroupFriendlyURL(layout), themeDisplay));
				locationDetailsList.add(locationVo);
				
			}
		}
		return locationDetailsList;
		
		
	}
	
	/**
	 * Get Group Frienly URL by passing layout object
	 * @param layout
	 * @return
	 */
	public static String getGroupFriendlyURL(Layout layout) {

		String groupFriendlyURL = StringPool.BLANK;
		if (Validator.isNotNull(layout)) {

			try {
				groupFriendlyURL = layout.getGroup().getFriendlyURL();
			} catch (PortalException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (SystemException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return groupFriendlyURL;

	}

	private static final Log LOGGER = LogFactoryUtil.getLog(LocationTrackerReportsUtil.class);
}
