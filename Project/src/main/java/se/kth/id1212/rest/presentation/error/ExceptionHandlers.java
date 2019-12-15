package se.kth.id1212.rest.presentation.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import se.kth.id1212.rest.exception.CustomerNotFoundException;
import se.kth.id1212.rest.exception.OrderNotFoundException;
import se.kth.id1212.rest.exception.IllegalTransactionException;

/**
 * Handles all the exceptions thrown in this application.
 * 
 * @author Antonio
 *
 */
@ControllerAdvice
@ResponseBody
class ExceptionHandlers {
	private final String INVALID_METHOD_ARGUMENTS = "Invalid method arguments";
	private final String METHOD_ARGUMENT_TYPE_MISMATCH = "The type of the given arguments are wrong";
	
	/**
	 * Handles <code>CustomerNotFoundException</code>s.
	 * 
	 * @param exc The exception with the message.
	 * @return the exception message.
	 */
	@ExceptionHandler({CustomerNotFoundException.class, OrderNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ErrorResponse customerNotFoundExceptionHandler(Exception exc) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), exc.getMessage());
	}
	
	/**
	 * Handles <code>IllegalTransactionException</code>s.
	 * 
	 * @param exc The exception with the message.
	 * @return the exception message.
	 */
	@ExceptionHandler(IllegalTransactionException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	ErrorResponse illegalTransactionExceptionHandler(IllegalTransactionException exc) {
		return new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), exc.getMessage());
	}
	
	/**
	 * Handles <code>NoHandlerException</code>s.
	 * 
	 * @param exc The exception.
	 * @return the exception message.
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ErrorResponse noHandlerFoundExceptionHanlder(NoHandlerFoundException exc) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), exc.getMessage());
	}
	
	/**
	 * Handles <code>MissingServletRequestParameterException</code>s.
	 * 
	 * @param exc The exception.
	 * @return the exception message.
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponse missingServletRequestParameterException( MissingServletRequestParameterException exc){
		return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), exc.getMessage());
	}
	
	/**
	 * Handler for transaction failures, e.g constraint violations. Could
	 * also be a responsibility of the front-end to validate such constraints.
	 * 
	 * @param exc The <code>TransactionSystemException</code>.
	 * @return the exception message.
	 */
	/*
	@ExceptionHandler(TransactionSystemException.class)
	ResponseEntity<RepresentationModel<?>> handleRollbackException(TransactionSystemException exc) {
		Throwable ex = exc.getOriginalException();
		if(ex instanceof RollbackException) {
			Throwable e = ex.getCause();
			if(e instanceof ConstraintViolationException) {
				StringJoiner str = new StringJoiner(", ");
				((ConstraintViolationException) e).getConstraintViolations().forEach(violation -> {
					str.add(violation.getMessage());
				});
				
				return toResponseEntity(HttpStatus.BAD_REQUEST, str.toString());
			}
			return toResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
		return toResponseEntity(HttpStatus.BAD_REQUEST, exc.getMessage());
	}
	*/
	

	/**
	 * Handler for invalid method arguments. For example, missing fields in
	 * <code>Customer</code>s.
	 * 
	 * @param exc The <code>MethodArgumentNotValidException</code>.
	 * @return a list over all the violations.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	 ViolationResponse constraintViolationExceptionHandler(MethodArgumentNotValidException exc){
		ViolationResponse vr = new ViolationResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), INVALID_METHOD_ARGUMENTS);
		List<Violation> violations = vr.getViolations();
		exc.getBindingResult().getFieldErrors().forEach(fieldError -> {
			violations.add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
		});
		return vr;
	}
	
	/**
	 * Handler for not supported methods.
	 * 
	 * @param exc The <code>HttpRequestMethodNotSupportedException</code>.
	 * @return the exception message.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException exc) {
		return new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), exc.getMessage());
	}
	
	/**
	 * Handler for method argument type mismatches.
	 * 
	 * @param exc The <code>MethodArgumentTypeMismatchException</code>.
	 * @return the exception message with the expected type.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exc) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), METHOD_ARGUMENT_TYPE_MISMATCH
				+ ", expected: " + exc.getRequiredType().getSimpleName());
	}
	
	/**
	 * Handles the exceptions that are not handled by any other exception handler.
	 * 
	 * @param exc the exception to be handled.
	 * @return the exception message.
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ErrorResponse handleException(Exception exc) {
		System.out.println(exc.getClass());
		exc.printStackTrace();
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exc.getMessage());
	}
	
	/*
	private ResponseEntity<RepresentationModel<?>> toResponseEntity(HttpStatus httpStatus, String msg){
		return ResponseEntity.status(httpStatus)
				.body(new VndErrors.VndError(httpStatus.getReasonPhrase(), msg));
	}
	*/
}
