package com.spatialize.web;

import hk.com.quantum.zonemgr.response.model.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping(value = "/floorplan.json", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody FloorPlan getFloorPlan() {
		FloorPlan fp = new FloorPlan();
		fp.imgurl="/spatialize/resources/images/section1.png";
		fp.sensors = createDevices();
		return fp;
	}
	
	@RequestMapping(value = "/floorplan.json", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Object saveFloorPlan() {
		return new Object();
	}
	
	private Collection<Device> createDevices() {
		List<Device> deviceList = new ArrayList<Device>();
		for (Tag tag: deviceSvc.getAvailableSensors()) {
			Device d = new Device();
			d.id = tag.id;
			
			d.imgurl = "/spatialize/resources/images";
			
			if (hasAttribute(tag, "humidity")) {
				d.imgurl += "/temperature-humidity-symbol.png";
				
			} else if (hasAttribute(tag, "temp")) {
				d.imgurl += "/temperature-symbol.png";
				
			} else if (hasAttribute(tag, "dooropen")) {
				d.imgurl += "/door-symbol.png";
				
				boolean doorOpen = Boolean.valueOf((String)tag.attributes.get("dooropen"));
				if (doorOpen)
					d.color = "red";
				else
					d.color = "green";
				
			} else if (hasAttribute(tag, "fluid")) {
				d.imgurl += "/fluid-symbol.png";
				
			} else {
				//d.imgurl += "/unknown.png";
				continue;
			}
			d.name = tag.id;
			d.notes = "";
			d.parameters = "";
			
			deviceList.add(d);
		}
		return deviceList;
	}
	
	private boolean hasAttribute(Tag tag, String attr) {
		return tag.attributes.containsKey(attr)
				&& tag.attributes.get(attr) != null;
	}
}
