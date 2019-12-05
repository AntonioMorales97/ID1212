package se.kth.id1212.currencyserv.currency.presentation.curr;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import se.kth.id1212.currencyserv.currency.application.CurrencyConverterService;
import se.kth.id1212.currencyserv.currency.domain.ConversionCounterDTO;
import se.kth.id1212.currencyserv.currency.domain.IllegalConversionTransactionException;

/**
 * Handles the HTTP requests to this application.
 * 
 * @author Antonio
 *
 */
@Controller
@Scope("session")
public class CurrencyConversionController {
	static final String DEFAULT_PAGE_URL = "/";
	static final String MAIN_PAGE_URL = "currency";
	static final String CONVERT_URL = "convert";
	static final String ADMIN_PAGE_URL = "admin";
	static final String UPDATE_RATE_URL = "update-rate";
	
	private static final String CONVERT_CURRENCY_FORM = "currencyConversionForm";
	private static final String CONVERSION_RATE_FORM = "conversionRateForm";
	private static final String CONVERSION_COUNTER_OBJ_NAME = "conversionCounter";
	private static final String SIMPLE_MESSAGE = "message";
	
	private ConversionCounterDTO currCounter;
	
	@Autowired
	private CurrencyConverterService convertService;
	
	/**
	 * No specified page will result in redirection to the main page URL.
	 * 
	 * @return a response that redirects the browser to the main page.
	 */
	@GetMapping(DEFAULT_PAGE_URL)
	public String showMainPage() {
		return "redirect:" + MAIN_PAGE_URL;
	}
	
	/**
	 * A HTTP GET request to the main page URL.
	 * 
	 * @param convertForm Used in the form for currency conversions.
	 * @param model <code>Model</code> objects used in the main page.
	 * @return the main page URL.
	 */
	@GetMapping("/" + MAIN_PAGE_URL)
	public String mainPage(CurrencyConversionForm convertForm, Model model) {
		convertForm.setConvertedAmount(0.0);
		convertForm.setAmount(0.0);
		model.addAttribute(CONVERT_CURRENCY_FORM, convertForm);
		return MAIN_PAGE_URL;
	}
	
	/**
	 * A HTTP POST request for currency conversion.
	 * 
	 * @param convertForm The form that holds the user input for currency
	 * conversion.
	 * @param bindingResult Validation result for the created convert form. 
	 * @param model <code>Model</code> objects used in the main page.
	 * @return the main page URL.
	 * @throws IllegalConversionTransactionException If input broke some 
	 * business rules.
	 */
	@PostMapping("/" + CONVERT_URL)
	public String convert(@Valid CurrencyConversionForm convertForm, BindingResult bindingResult,
			Model model) throws IllegalConversionTransactionException {
		if(bindingResult.hasErrors()) {
			convertForm.setConvertedAmount(0.0);
			model.addAttribute(CONVERT_CURRENCY_FORM, convertForm);
			return MAIN_PAGE_URL;
		}
		double convertedAmount = convertService.convertAmount(convertForm.getFromCurrency(), convertForm.getToCurrency(), convertForm.getAmount());
		convertForm.setConvertedAmount(convertedAmount);
		convertService.incrementConversionCounter();
		model.addAttribute(CONVERT_CURRENCY_FORM, convertForm);
		return MAIN_PAGE_URL;
	}
	
	/**
	 * A HTTP GET request to the admin page URL.
	 * 
	 * @param rateForm Used in the form for updating conversion rates.
	 * @param model <code>Model</code> objects used in the admin page.
	 * @return the admin page URL.
	 */
	@GetMapping("/" + ADMIN_PAGE_URL)
	public String adminPage(ConversionRateForm rateForm, Model model) {
		rateForm.setRate(0);
		this.currCounter = convertService.getCounter();
		if(this.currCounter != null) {
			model.addAttribute(CONVERSION_COUNTER_OBJ_NAME, this.currCounter);
		}
		model.addAttribute(CONVERSION_RATE_FORM, rateForm);
		return ADMIN_PAGE_URL;
	}
	
	/**
	 * A HTTP POST request to update the conversion rate between two
	 * currencies. This is for the admin interface.
	 * 
	 * @param rateForm Used in the form for updating conversion rates.
	 * @param bindingResult Validation result for the created rate form.
	 * @param model <code>Model</code> objects used in the admin page.
	 * @return the admin page URL.
	 * @throws IllegalConversionTransactionException if the operation could
	 * not be done.
	 */
	@PostMapping("/" + UPDATE_RATE_URL)
	public String updateConversionRate(@Valid ConversionRateForm rateForm, BindingResult bindingResult,
			Model model) throws IllegalConversionTransactionException {
		if(bindingResult.hasErrors()) {
			model.addAttribute(CONVERSION_COUNTER_OBJ_NAME, this.currCounter);
			model.addAttribute(CONVERSION_RATE_FORM, rateForm);
			return ADMIN_PAGE_URL;
		}
		convertService.setConversionRate(rateForm.getFromCurrency(), rateForm.getToCurrency(), rateForm.getRate());
		updateCurrentCounter();
		model.addAttribute(CONVERSION_COUNTER_OBJ_NAME, this.currCounter);
		model.addAttribute(CONVERSION_RATE_FORM, rateForm);
		model.addAttribute(SIMPLE_MESSAGE, "Successfully updated rate!");
		return ADMIN_PAGE_URL;
	}
	
	private void updateCurrentCounter() {
		this.currCounter = convertService.getCounter();
	}
	
}
