package com.pos.retailer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "master_data")
@Getter
@Setter
@NoArgsConstructor
public class MasterData extends Auditable{
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	@Size(max = 10)
	private String category;

	@Column(nullable = false)
	@Size(max = 20)
	private String abbreviation;

	@Column(nullable = false)
	@Size(max = 20)
	private String description;

	public MasterData(@Size(max = 10) String category, @Size(max = 20) String abbreviation,
			@Size(max = 20) String description) {
		super();
		this.category = category;
		this.abbreviation = abbreviation;
		this.description = description;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
