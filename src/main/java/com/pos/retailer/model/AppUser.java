package com.pos.retailer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.Setter;

@Entity

@Getter
@Setter
@Table(name = "users")
public class AppUser extends Auditable {
	private static final long serialVersionUID = 1L;

	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;
	private String lastName;
	@Column(unique = true, nullable = false)
	private String username;

	@Transient
	private boolean changePassword = false;

	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String roles;
	@Column(nullable = false)
	private boolean active = true;

	public AppUser() {
		super();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public AppUser(String firstName, String lastName, String username, String password, String roles, boolean active) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.active = active;

	}

	/*
	 * @JsonIgnore public void setPassword(String password) { this.password =
	 * password; }
	 */

}