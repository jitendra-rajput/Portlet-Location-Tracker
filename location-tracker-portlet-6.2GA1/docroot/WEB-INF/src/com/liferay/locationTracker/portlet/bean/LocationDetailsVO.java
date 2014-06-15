package com.liferay.locationTracker.portlet.bean;

public class LocationDetailsVO
{
    private String name;
    private String group;
    private String friendlyURL;
    private String pageUrl;
    private boolean privateLayout;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getFriendlyURL() {
		return friendlyURL;
	}
	public void setFriendlyURL(String friendlyURL) {
		this.friendlyURL = friendlyURL;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public boolean isPrivateLayout() {
		return privateLayout;
	}
	public void setPrivateLayout(boolean privateLayout) {
		this.privateLayout = privateLayout;
	}

 
}
