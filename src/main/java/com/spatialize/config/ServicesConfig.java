package com.spatialize.config;

import hk.com.quantum.zonemgr.ZoneManagerConfig;
import hk.com.quantum.zonemgr.ZoneManagerService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spatialize.io.DeviceServices;

@Configuration
public class ServicesConfig {

	@Bean
	public DeviceServices configureDeviceService() {
		return new DeviceServices();
	}
	
	@Bean 
	public ZoneManagerService configureZoneManagerService() {
		ZoneManagerService svc = new ZoneManagerService(ZoneManagerConfig.getInstance());
		return svc;
	}
}
