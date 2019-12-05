package se.kth.id1212.currencyserv.currency.presentation.error;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;

import se.kth.id1212.currencyserv.currency.domain.IllegalConversionTransactionException;

/**
 * Global exception handling. Will handle all exception thrown in 
 * this application.
 * 
 * @author Antonio
 *
 */
@Controller
@ControllerAdvice
public class ExceptionHandlers implements ErrorController {
	public static final String ERROR_PAGE_URL = "error";
	public static final String ERROR_TYPE_KEY = "errorType";
	public static final String GENERIC_ERROR = "Operation Failed.";
	public static final String ERROR_INFO_KEY = "errorInfo";
	public static final String ERROR_PATH = "failure";

	/**
	 * This will handle the <code>IllegalConversionTransactionException</code>s
	 * thrown in this application.
	 * 
	 * @param exc The caught exception.
	 * @param model <code>Model</code> objects used in the error page.
	 * 
	 * @return the error page URL.
	 */
	@ExceptionHandler(IllegalConversionTransactionException.class)
    @ResponseStatus(HttpStatus.OK)
	public String handleException(IllegalConversionTransactionException exc, Model model) {
		model.addAttribute(ERROR_TYPE_KEY, GENERIC_ERROR);
		model.addAttribute(ERROR_INFO_KEY, exc.getMessage());
		return ERROR_PAGE_URL;
	}
	
	/**
     * This will handle all the exceptions that are not handled by any
     * other exception handler.
     *
     * @return the error page URL.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception exc, Model model) {
        model.addAttribute(ERROR_TYPE_KEY, GENERIC_ERROR);
        model.addAttribute(ERROR_INFO_KEY, "Something went wrong in server side. What you were"
        		+ " trying to do maybe is not supported :(");
        return ERROR_PAGE_URL;
    }
    
    /**
     * If we are redirected to the configured error path.
     * See application.properties.
     * 
     * @param request for request of information for Http servlets.
     * @param response for sending specific Http responses
     * @param model <code>Model</code> objects used in the error page.
     * 
     * @return the error page URL
     */
    @GetMapping("/" + ERROR_PATH)
    public String handleHttpError(HttpServletRequest request, HttpServletResponse response, Model model) {
        int statusCode = Integer.parseInt(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute(ERROR_TYPE_KEY, "HTTP 404");
            model.addAttribute(ERROR_INFO_KEY, "Ops, page not found :(");
            response.setStatus(statusCode);
        } else {
            model.addAttribute(ERROR_TYPE_KEY, GENERIC_ERROR);
            model.addAttribute(ERROR_INFO_KEY, "Something went very wrong again :(");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ERROR_PAGE_URL;
    }
    
    @Override
    public String getErrorPath() {
        return "/" + ERROR_PATH;
    }
}
