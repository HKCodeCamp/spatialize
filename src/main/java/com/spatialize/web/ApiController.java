package com.spatialize.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spatialize.domain.FloorPlan;

@RequestMapping("/api")
@Controller
public class ApiController {

	@RequestMapping(value = "/floorplan.json", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody FloorPlan getFloorPlan() {
		FloorPlan fp = new FloorPlan();
		fp.imgurl="http://www.google.com";
		return fp;
	}
}
