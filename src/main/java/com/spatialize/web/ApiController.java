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
		fp.imgurl="http://www.google.com";
		fp.sensors = createDevices();
		return fp;
	}
	
	private Collection<Device> createDevices() {
		List<Device> deviceList = new ArrayList<Device>();
		for (Tag tag: deviceSvc.getAvailableSensors()) {
			Device d = new Device();
			d.id = tag.id;
			d.imgurl = "/spatialize/resources/images/temperature-symbol.png";
			d.name = null;
			d.notes = "";
			d.parameters = "";
			
			deviceList.add(d);
		}
		return deviceList;
	}
}
