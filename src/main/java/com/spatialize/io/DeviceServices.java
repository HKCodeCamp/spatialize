package com.spatialize.io;

import hk.com.quantum.zonemgr.ZoneManagerService;
import hk.com.quantum.zonemgr.response.TagListResponse;
import hk.com.quantum.zonemgr.response.model.Tag;

import java.util.Collection;

import javax.annotation.Resource;

public class DeviceServices {

	@Resource
	private ZoneManagerService zmservice;
	
	public Collection<Tag> getAvailableSensors() {
		TagListResponse resp = zmservice.tagList();
		return resp.tags.values();
	}
}
