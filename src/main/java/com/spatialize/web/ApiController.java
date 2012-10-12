package com.spatialize.web;

import hk.com.quantum.zonemgr.response.model.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

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
			
			if (tag.attributes.containsKey("humidity")) {
				d.imgurl += "/temperature-humidity-symbol.png";
				
			} else if (tag.attributes.containsKey("temp")) {
				d.imgurl += "/temperature-symbol.png";
			} else if (tag.attributes.containsKey("door")) {
				d.imgurl += "/door-symbol.png";
				
			} else if (tag.attributes.containsKey("fluid")
					&& "null".equalsIgnoreCase((String) tag.attributes
							.get("fluid")) == false) {
				d.imgurl += "/fluid-symbol.png";

			} else {
				d.imgurl += "/unknown.png";
			}
			d.name = tag.id;
			d.notes = "";
			d.parameters = "";
			
			deviceList.add(d);
		}
		return deviceList;
	}
}
