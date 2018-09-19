package com.pos.retailer.component;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AppHealthIndicator implements HealthIndicator{

	@Override
	public Health health() {
		// TODO Auto-generated method stub
		return Health.status("Checking Health").build();
	}

}
