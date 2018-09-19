package com.pos.retailer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends Auditable {
	
	private static final long serialVersionUID = 6783655829850978880L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Size(max = 60)
	private String fullName;
	
	@Size(max = 15)
	@Column(nullable = true)
	private String staffCode;
	
	@Size(max = 60)
	@NotNull
	private String loginUsername;
	
	@Size(max = 60)
	@Column(nullable = true)
	private String password;
	
	@Size(max = 15)
	@NotNull
	private String role;
	
	
}
