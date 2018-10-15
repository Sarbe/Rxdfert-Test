package com.pos.retailer.model;

import java.util.ArrayList;
import java.util.List;

import com.pos.retailer.repository.model.ProductSummary;

import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ProductSummaryDto {
	private ProductSummary summary;
	private List<Product> products = new ArrayList<>();
}
