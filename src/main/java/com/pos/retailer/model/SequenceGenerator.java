package com.pos.retailer.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "seq_generator")
public class SequenceGenerator {
	@Id
	private String name;
	private Long seq;

	public SequenceGenerator() {
	}
	
	public void increase() {
		this.seq++;
	}

}
