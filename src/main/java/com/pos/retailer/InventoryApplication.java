package com.pos.retailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.config.MyCorsFilter;
import com.pos.retailer.model.AppUser;
import com.pos.retailer.model.MasterData;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.repository.AppUserRepository;
import com.pos.retailer.service.MasterDataService;
import com.pos.retailer.service.ProductService;

@SpringBootApplication
@ComponentScan(basePackages = { "com.pos" })
@EnableCaching
public class InventoryApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<MyCorsFilter> corsFilterRegistration() {
		FilterRegistrationBean<MyCorsFilter> registrationBean = new FilterRegistrationBean<MyCorsFilter>(
				new MyCorsFilter());
		registrationBean.setName("CORS Filter");
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	private ProductService productService;

	@Autowired
	private AppUserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {

		// check for inventory status and create a touch file
		boolean staus = productService.checkInvnetoryClosingStatus();
		AppConstant.invStatus = staus;

		///////////////
		if (masterDataService.getUOMDetailsCount() == 0) {
			List<MasterData> datas = new ArrayList<>();
			datas.add(new MasterData("UOM", "KG", "Kilo"));
			datas.add(new MasterData("UOM", "pkt", "Packet"));
			datas.add(new MasterData("UOM", "pcs", "Pieces"));
			datas.add(new MasterData("UOM", "Lt", "Litre"));
			datas.add(new MasterData("TAX", "5", "5%"));
			datas.add(new MasterData("TAX", "12", "12%"));
			datas.add(new MasterData("TAX", "18", "18%"));
			datas.add(new MasterData("TAX", "28", "28%"));

			masterDataService.saveMasterData(datas);

		}

		long sellerCnts = masterDataService.getSellerDetailsCount();
		if (sellerCnts == 0) {

			SellerDetails seller = new SellerDetails();
			seller.setShopName("SS GALLERY");
			seller.setOwnership("OWNER");
			seller.setAddressLine1("Patia, bhubaneswar , pin 75945");
			seller.setBusinessPhone1("987456321");
			seller.setBusinessPhone2("4567890");
			seller.setCity("bbsr");
			seller.setFeedbackEmail("ss@s.com");
			seller.setGstInNumber("GSTIN345678678");
			seller.setPincode("759145");

			masterDataService.saveSellerDetails(seller);
		}

		/*
		 * // Save roles master data
		 * 
		 * List<Role> roles = new ArrayList<>();
		 * 
		 * roles.add(new Role("Owner", "ROLE_OWNER",
		 * "System owner to initiate the system.")); roles.add(new Role("Administrator",
		 * "ROLE_ADMIN", "System Administrator to manage the POS system."));
		 * roles.add(new Role("Clerk", "ROLE_CLERK",
		 * "System Clerk to interact to the consumer and use the system."));
		 * roles.add(new Role("Waiter", "ROLE_WAITER",
		 * "Restaurant waiter to interact to the consumer and use the system."));
		 * roles.add(new Role("Delivery Agent", "ROLE_DELIVERY",
		 * "Delivery agent to deliver the home delivery orders.")); roles.add(new
		 * Role("Kichen", "ROLE_KITCHEN",
		 * "System Kitchen cooks to view the system orders."));
		 * 
		 * this.roleRepository.saveAll(roles);
		 * 
		 * 
		 * List<InventoryActivity> inventoryActivities = new ArrayList<>();
		 * 
		 * inventoryActivities.add(new InventoryActivity("Stock Open", "STOCK_OPENING",
		 * "Day open for the stock for inventory operations.", false));
		 * inventoryActivities.add(new InventoryActivity("Stock Receive",
		 * "STOCK_RECEIVE", "Received stock added to the inventory.", true));
		 * inventoryActivities.add(new InventoryActivity("Transfer In", "TRANSFER_IN",
		 * "Transferred from another branch and added to this inventory.", true));
		 * inventoryActivities.add(new InventoryActivity("Transfer Out", "TRANSFER_OUT",
		 * "Transferred out from this inventory to another branch.", true));
		 * inventoryActivities.add(new InventoryActivity("Stock Dump", "STOCK_DUMP",
		 * "Wasted or soiled stock out of this inventory.", true));
		 * inventoryActivities.add(new InventoryActivity("Stock Consumed",
		 * "STOCK_CONSUMED", "Item from inventory used up to for an order.", false));
		 * inventoryActivities.add(new InventoryActivity("Stock Close", "STOCK_CLOSING",
		 * "Day closed for the stock for inventory operations.", false));
		 * 
		 * this.inventoryActivityRepository.saveAll(inventoryActivities);
		 * 
		 * 
		 * // Save units master data
		 * 
		 * List<Unit> units = new ArrayList<>();
		 * 
		 * units.add(new Unit("Kilogram", "Weight Standard Unit", "Kg", "Kg"));
		 * units.add(new Unit("Gram", "Weight Standard Unit", "gram", "grams"));
		 * units.add(new Unit("Kilolitre", "Liquid Standard Unit", "Kl", "Kl"));
		 * units.add(new Unit("Litre", "Liquid Standard Unit", "litre", "litres"));
		 * units.add(new Unit("Piece", "Consumable Item Unit", "piece", "pieces"));
		 * units.add(new Unit("Packet", "Packaged Item Unit", "packet", "packets"));
		 * units.add(new Unit("Sachet", "Packaged Item Unit", "sachet", "sachets"));
		 * units.add(new Unit("Plate", "SalesOrder item unit", "plate", "plates"));
		 * units.add(new Unit("Glass", "Liquid item order unit", "glass", "glasses"));
		 * units.add(new Unit("Cup", "Liquid beverage item order unit", "cup", "cups"));
		 * 
		 * this.unitRepository.saveAll(units);
		 * 
		 * 
		 * // Save currency master data
		 * 
		 * List<Currency> currencies = new ArrayList<>();
		 * 
		 * currencies.add(new Currency("Indian Rupee", "Rs.", "INR", "Indian Currency",
		 * true)); currencies.add(new Currency("US Dollar", "$", "USD", "USA Currency",
		 * false));
		 * 
		 * this.currencyRepository.saveAll(currencies);
		 * 
		 * 
		 * // Save product type master data
		 * 
		 * List<ProductType> productTypes = new ArrayList<>();
		 * 
		 * productTypes.add(new ProductType("Singular Product", "SINGULAR",
		 * "No Recipe for these products.")); productTypes.add(new
		 * ProductType("Complex Product", "COMPLEX",
		 * "Recipe mandatory for these products."));
		 * 
		 * this.productTypeRepository.saveAll(productTypes);
		 * 
		 * 
		 * // Save tax master data
		 * 
		 * List<Tax> taxes = new ArrayList<>();
		 * 
		 * taxes.add(new Tax("GST_5", 5)); taxes.add(new Tax("GST_12", 12));
		 * taxes.add(new Tax("GST_18", 18)); taxes.add(new Tax("GST_28", 28));
		 * 
		 * this.taxRepository.saveAll(taxes);
		 * 
		 * 
		 * // Save master category data
		 * 
		 * 
		 * 
		 * 
		 * 
		 * List<AppUser> students = Arrays.asList( new AppUser("Reza", "Khalid",
		 * "rezakhalid", "password", new String[] { "ROLE_USER" }), new
		 * AppUser("Satyaveer", "Singh", "satyveer_singh", "password", new String[] {
		 * "ROLE_USER" }), new AppUser("Satish", "Yadav", "satishyadav", "password", new
		 * String[] { "ROLE_USER" }), new AppUser("Farzan", "Ahmad", "farzan_ahmad",
		 * "password", new String[] { "ROLE_USER" })); userRepository.saveAll(students);
		 * 
		 */
		// userRepository.deleteAll();
		//
		// userRepository.save(new AppUser("ss", "sss", "sarbe",
		// bCryptPasswordEncoder.encode("123456"), "ROLE_ADMIN"));
		// userRepository.save(new AppUser("ss", "sss", "susa",
		// bCryptPasswordEncoder.encode("123456"), "ROLE_CLERK"));

		Optional<AppUser> optUser = userRepository.findByUsername("posAdmin");
		if (!optUser.isPresent()) {
			userRepository.save(new AppUser("POS ADMIN", "AdminAccount", "posAdmin",
					bCryptPasswordEncoder.encode("posAdmin"), AppConstant.ROLE_SUPER_USER, true));
		}

	}

}
