package com.pos.retailer.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.MultiPayment;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.service.CommonOrderService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private CommonOrderService commonOrderService;

	@GetMapping
	public ResponseEntity<?> getDashboardCounts() {
		return new ResponseWrapper<>(HttpStatus.OK, this.commonOrderService.getCounts()).sendResponse();
	}

	@GetMapping("/test")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> test(Principal p) {

		System.out.println(p.getName());
		this.commonOrderService.getOrderNbrs();
		return new ResponseWrapper<>(HttpStatus.OK, this.commonOrderService.getCounts()).sendResponse();
	}

	@GetMapping("/token/verify")
	public ResponseEntity<?> verifyToken(Authentication auth) {
		System.out.println(auth.getAuthorities());
		String authority = "";
		for (GrantedAuthority grantedAuthhority : auth.getAuthorities()) {
			authority = grantedAuthhority.getAuthority();
		}

		return new ResponseWrapper<>("All User Details", HttpStatus.OK, authority).sendResponse();
	}

	///////////////////////

	/*
	 * @PostMapping("/search") public ResponseEntity<?> searchOrders(@RequestBody
	 * SearchCriteria search) throws GenericException { return new
	 * ResponseWrapper<>(HttpStatus.OK,
	 * this.commonOrderService.getSummaryBySearchCriteriaGroupedBy(search))
	 * .sendResponse(); }
	 */

	@PostMapping("/multiPayment")
	public ResponseEntity<?> multiPayment(@RequestBody MultiPayment paymnet) throws GenericException {

		List<OutstandingSummary> outStandingsList = commonOrderService.multiplePayment(paymnet);
		OutstandingSummary outStanding = null;
		if (!ObjectUtils.isEmpty(outStandingsList) && outStandingsList.size() > 0) {
			outStanding = outStandingsList.get(0);
		}
		return new ResponseWrapper<>("Payment Details updated.", HttpStatus.OK, outStanding).sendResponse();
	}

	@PostMapping("/party/outstanding")
	public ResponseEntity<?> partyDetails(@RequestBody OutstandingSummary dtl) {
		return new ResponseWrapper<>("Party Details", HttpStatus.OK,
				commonOrderService.getDetailedOutstandingsForOneParty(dtl.getOrderType(), dtl.getPartyName()))
						.sendResponse();
	}

}
