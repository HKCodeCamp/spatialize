package com.spatialize.web;

import hk.com.quantum.zonemgr.response.model.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spatialize.domain.Device;
import com.spatialize.domain.FloorPlan;
import com.spatialize.io.DeviceServices;

@RequestMapping("/api")
@Controller
public class ApiController {
	
	private static final Logger log = LoggerFactory.getLogger(ApiController.class);
	
	@Resource
	private DeviceServices deviceSvc;
	
	private static Object floorPlanLock = new Object();
	private static FloorPlan singleFloorPlan;

	@RequestMapping(value = "/floorplan.json", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody FloorPlan getFloorPlan() {
		synchronized(floorPlanLock) {
			if (singleFloorPlan == null) {
				singleFloorPlan = new FloorPlan();
				singleFloorPlan.imgurl="/spatialize/resources/images/floorplan1.jpg";
				singleFloorPlan.sensors = createDevices();
			}
		}
		updateSensor(singleFloorPlan);
		if(log.isDebugEnabled()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writeValueAsString(singleFloorPlan);
				log.debug("Write json: " + json);
			} catch (Exception e) {
				log.error("Error deserializing floor plan to json string.", e);
			}
		}
		return singleFloorPlan;
	}
	
	private void updateSensor(FloorPlan floorPlan) {
		for (Tag tag: deviceSvc.getAvailableSensors()) {
			for (Device d: floorPlan.sensors) {
				if (d.id != null && d.id.equals(tag.id)){
					populateDeviceFromTag(d, tag);
				}
			}
		}
	}

	@RequestMapping(value = "/floorplan.json", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Object saveFloorPlan(HttpServletRequest req,
			@RequestParam String jsonstring) {
		log.debug("Posting: " + jsonstring);
		Set<String> keySet = req.getParameterMap().keySet();
		for(String key: keySet) {
			log.debug(key + ":" + req.getParameter(key));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			FloorPlan fp = mapper.readValue(jsonstring, FloorPlan.class);
			log.debug("Floor Plan Serialized: " + fp);
			synchronized(floorPlanLock) {
				singleFloorPlan = fp;
			}
			return fp;
		} catch (Exception e) {
			log.error("Error Parsing Post Json String: '{}'", new Object[] { jsonstring }, e);
			return null;
		} 
	}
	
	private Collection<Device> createDevices() {
		List<Device> deviceList = new ArrayList<Device>();
		for (Tag tag: deviceSvc.getAvailableSensors()) {
			Device d = new Device();
			d.id = tag.id;
			d.name = tag.id;
			d.notes = "";
			d.parameters = "";
			d.color = "green";
			
			boolean populated = populateDeviceFromTag(d, tag);
			
			if (populated == true)
				deviceList.add(d);
		}
		return deviceList;
	}
	
	private boolean populateDeviceFromTag(Device d, Tag tag) {
		d.imgurl = "/spatialize/resources/images";
		
		if (hasAttribute(tag, "humidity")) {
			d.imgurl += "/temperature-humidity-symbol.png";
			d.notes = "Humidity: " + tag.attributes.get("humidity") + "%";
			d.notes += "<br/>Temperature: " + tag.attributes.get("temp") + "C";
			
		} else if (hasAttribute(tag, "temp")) {
			d.imgurl += "/temperature-symbol.png";
			d.notes = "Temperature: " + tag.attributes.get("temp") + "C";
			
		} else if (hasAttribute(tag, "dooropen")) {
			d.imgurl += "/door-symbol.png";
			d.notes = "Door open?: " + tag.attributes.get("dooropen");
			
			boolean doorOpen = Boolean.valueOf((String)tag.attributes.get("dooropen"));
			if (doorOpen)
				d.color = "red";
			else
				d.color = "green";
			
		} else if (hasAttribute(tag, "fluid")) {
			d.imgurl += "/fluid-symbol.png";
			d.notes = "Fluid detected: " + tag.attributes.get("fluid");
			
			boolean fluid = Boolean.valueOf((String)tag.attributes.get("fluid"));
			if (fluid)
				d.color = "red";
			else
				d.color = "green";
			
		} else {
			//d.imgurl += "/unknown.png";
			return false;
		}
		return true;
	}
	
	private boolean hasAttribute(Tag tag, String attr) {
		return tag.attributes.containsKey(attr)
				&& tag.attributes.get(attr) != null;
	}
}
