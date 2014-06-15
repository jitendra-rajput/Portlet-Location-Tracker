package com.liferay.locationTracker.portlet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.liferay.locationTracker.portlet.bean.LocationDetailsVO;
import com.liferay.locationTracker.portlet.util.LocationTrackerReportsUtil;
import com.liferay.locationTracker.portlet.util.LocationTrackerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class LocationTrackerPortlet extends MVCPortlet
{
  
    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException
    {
        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
        renderRequest.setAttribute("portletList", LocationTrackerUtil.getPortletList(themeDisplay.getCompanyId(),themeDisplay.getLocale()));
        renderRequest.setAttribute("searchContainer", LocationTrackerUtil.getSearchContainer(renderRequest,renderResponse));
        String portletId = ParamUtil.getString(renderRequest, "portletId");
        renderRequest.setAttribute("selectedPortletId", portletId);
        //renderRequest.setAttribute("portletDetailList", LocationTrackerUtil.findPortlet(renderRequest));
        include(viewJSP, renderRequest, renderResponse);
    }
    
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) {

		try {

			String portletId = ParamUtil
					.getString(resourceRequest, "portletId");
			LOGGER.error("portlet id--" + portletId);
			 SimpleDateFormat timeStampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_FOR_EXCEL);
	         PortletSession session = resourceRequest.getPortletSession();
	         String timeStamp = timeStampFormat.format(Calendar.getInstance().getTime());
			  String fileName = "Location_Tracker" + timeStamp + ".xls"; 
			List<LocationDetailsVO> locationDetailList = LocationTrackerReportsUtil
					.getLocationDetailsVoList(resourceRequest);
			JRBeanCollectionDataSource reportList = new JRBeanCollectionDataSource(
					locationDetailList);
			JasperReport report = JasperCompileManager.compileReport(session
					.getPortletContext().getRealPath(
							"/reports/location_tracker_report.jrxml"));
			JasperPrint print = JasperFillManager.fillReport(report,
					new HashMap<String, Object>(), reportList);

			OutputStream os = resourceResponse.getPortletOutputStream();
			JRXlsExporter exporterXLS = new JRXlsExporter();
			exporterXLS
					.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, os);
			exporterXLS
					.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
							Boolean.FALSE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);
			exporterXLS.exportReport();

			resourceResponse.setContentType("application/x-excel");
			resourceResponse.setProperty("Content-Disposition",
					"attachment; filename=" + fileName);

		} catch (JRException jre) {
			LOGGER.error("Error in exporting the resluts:" + jre.getMessage(),
					jre);
		} catch (Exception e) {
			LOGGER.error("Error in exporting the resluts:" + e.getMessage(), e);
		}

	}

    protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException,
            PortletException
    {

        PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);

        if (portletRequestDispatcher == null)
        {
            LOGGER.error(path + " is not a valid include");
        } else
        {
            portletRequestDispatcher.include(renderRequest, renderResponse);
        }
    }

    @Override
    public void init() throws PortletException
    {
        viewJSP = getInitParameter("view-template");
    }
  
    
    private String viewJSP;
    private static final String TIMESTAMP_FORMAT_FOR_EXCEL = "yyyy-MM-dd";
    private static final Log LOGGER = LogFactoryUtil.getLog(LocationTrackerPortlet.class);
}
