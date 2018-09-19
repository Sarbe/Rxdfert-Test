package com.pos.retailer.service.Impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class Test{

/*

addInventoryTransaction(InventoryTransaction inventoryTransaction) throws GenericException {
		logger.info("ProductServiceImpl.addInventoryRecord()");
		
		Product dbProduct = this.productRepository.findById(inventoryTransaction.getProductId()).orElse(null);
		
		// Check the inventory activity and update product accordingly
		
		switch (inventoryTransaction.getActivityCode()) {
		
		case AppConstants.STOCK_OPEN:
			
			// Stock opening
			
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				inventoryTransaction = null;
			}
			break;
			
		case AppConstants.STOCK_RECEIVE:
			
			// Stock received
			
			dbProduct.setStockQty(dbProduct.getStockQty() + inventoryTransaction.getQty());
			dbProduct.setPrice(inventoryTransaction.getPrice());
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction.setPartyName(StringUtils.left(inventoryTransaction.getPartyName(), 50));
						inventoryTransaction.setGstinNumber(StringUtils.left(inventoryTransaction.getGstinNumber(), 30));
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				inventoryTransaction = null;
			}
			break;
			
		case AppConstants.STOCK_TRANSFER_IN:
			
			// Transfer in
			
			dbProduct.setStockQty(dbProduct.getStockQty() + inventoryTransaction.getQty());
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction.setPartyName(StringUtils.left(inventoryTransaction.getPartyName(), 50));
						inventoryTransaction.setGstinNumber(StringUtils.left(inventoryTransaction.getGstinNumber(), 30));
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				inventoryTransaction = null;
			}
			break;
			
		case AppConstants.STOCK_TRANSFER_OUT:
			
			// Transfer out
			
			dbProduct.setStockQty(dbProduct.getStockQty() - inventoryTransaction.getQty());
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction.setPartyName(StringUtils.left(inventoryTransaction.getPartyName(), 50));
						inventoryTransaction.setGstinNumber(StringUtils.left(inventoryTransaction.getGstinNumber(), 30));
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				return null;
			}
			break;
			
		case AppConstants.STOCK_DUMP:
			
			// Stock dump
			
			dbProduct.setStockQty(dbProduct.getStockQty() - inventoryTransaction.getQty());
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction.setReason(StringUtils.left(inventoryTransaction.getReason(), 100));
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				return null;
			}
			break;
			
			
		case AppConstants.STOCK_CONSUME:
			
			// Stock consumption
			
			dbProduct.setStockQty(dbProduct.getStockQty() - inventoryTransaction.getQty());
			
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						InventoryTransaction dbInventoryTransaction = this.inventoryTransactionRepository.findLastByProductIdAndActivityNumber(dbProduct.getId(), AppConstants.STOCK_CONSUME);
						
						if(dbInventoryTransaction == null)	{
							dbInventoryTransaction = new InventoryTransaction(
									AppConstants.STOCK_CONSUME, 0, 
									dbProduct.getId(), dbProduct.getName(), 
									0, null, null, 
									dbProduct.getPrice(), 
									"Stock consumptions for the day.");
							
							dbInventoryTransaction = this.inventoryTransactionRepository.save(dbInventoryTransaction);
						}
						
						dbInventoryTransaction.setNumberOfTransactions(dbInventoryTransaction.getNumberOfTransactions() + 1);
						dbInventoryTransaction.setQty(dbInventoryTransaction.getQty() + inventoryTransaction.getQty());
						inventoryTransaction = this.inventoryTransactionRepository.save(dbInventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				return null;
			}
			
			break;
			
		case AppConstants.STOCK_CLOSE:
			
			// Stock closing
			
			dbProduct.setStockQty(dbProduct.getStockQty());
			if(this.updateProduct(dbProduct) != null)	{
				if(this.updateRecipeItemsAvailability(dbProduct.getId(), dbProduct.getStockQty()) != null)	{
					if(this.updateFoodAvailability() != null) {
						inventoryTransaction = this.inventoryTransactionRepository.save(inventoryTransaction);
					}
					else	{
						inventoryTransaction = null;
					}
				}
				else	{
					inventoryTransaction = null;
				}
			}
			else	{
				inventoryTransaction = null;
			}
			break;

		default:
			break;
			
		}
		
		return inventoryTransaction;
		
	}
	
	@Override
	public List<InventoryTransaction> closeAllProductsInInventory() {
		logger.info("ProductServiceImpl.closeAllProductsInInventory()");
		
		List<Product> products = new ArrayList<>();
		
		List<InventoryTransaction> inventoryTransactions = new ArrayList<>();
		
		products = this.productRepository.findByMasterCategoryAbbreviation(AppConstants.CONSUMABLE_CATEGORY);
		products.addAll(this.productRepository.findByMasterCategoryAbbreviation(AppConstants.INGREDIENT_CATEGORY));
		products.addAll(this.productRepository.findByMasterCategoryAbbreviationAndHasRecipeFalse(AppConstants.FOOD_CATEGORY));
		
		for(Product product : products)	{
			
			InventoryTransaction inventoryTransaction = this.inventoryTransactionRepository.findLastByProductId(product.getId()).orElse(new InventoryTransaction());
			
			if(inventoryTransaction.getActivityCode() == AppConstants.STOCK_CLOSE) {
				continue;
			}
			else	{
				inventoryTransactions.add(
						new InventoryTransaction(
								AppConstants.STOCK_CLOSE,
								product.getStockQty(),
								product.getId(), 
								product.getName(), 
								1, null, null, 
								product.getPrice(),
								"Stock Closing for the day."));
			}
		}
		
		if(inventoryTransactions.isEmpty())	{
			return inventoryTransactions;
		}
		
		return this.inventoryTransactionRepository.saveAll(inventoryTransactions);
	}
	
	@Override
	public boolean checkInventoryClosed() throws Exception {
		logger.info("ProductServiceImpl.checkInventoryClosed()");
		
		List<Product> products = new ArrayList<>();
		boolean closed = true;
		
		//try	{
			products = this.productRepository.findByMasterCategoryAbbreviation(AppConstants.CONSUMABLE_CATEGORY);
			products.addAll(this.productRepository.findByMasterCategoryAbbreviation(AppConstants.INGREDIENT_CATEGORY));
			products.addAll(this.productRepository.findByMasterCategoryAbbreviationAndHasRecipeFalse(AppConstants.FOOD_CATEGORY));
			
			for(Product product : products)	{
				
				InventoryTransaction optInventoryTransaction = this.inventoryTransactionRepository.findLastByProductId(product.getId()).orElseThrow(() -> new NullPointerException());
				
				if(optInventoryTransaction.getActivityCode() == AppConstants.STOCK_CLOSE) {
					continue;
				}
				else	{
					closed = false;
					break;
				}
			}
//		}	catch (Exception e) {
//		
//			logger.info("Excepton generated");
//			e.printStackTrace();
//			closed = true;
//			
//		}
		
		return closed;
	}


 */
}
