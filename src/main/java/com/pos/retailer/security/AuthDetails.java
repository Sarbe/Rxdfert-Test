package com.pos.retailer.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDetails {
	private String authorization;
	private String roles;
	
}
