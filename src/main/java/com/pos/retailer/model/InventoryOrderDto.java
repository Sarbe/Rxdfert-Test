
package com.pos.retailer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class InventoryOrderDto {
	InventoryOrder purchase;
	List<InventoryOrderDetails> purchaseDetails = new ArrayList<>();

}
