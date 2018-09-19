package com.pos.retailer.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

	public CustomExceptionHandler() {
		logger.info("CustomExceptionHandler initialized");
	}

	// 1.
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		ApiError apiError = new ApiError("EXC_001", HttpStatus.BAD_REQUEST, ex.getMessage(), errors);
		logger.error("Error Occured >>" + ex.getMessage());
		return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);

	}

	// 2.
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String error = ex.getParameterName() + " parameter is missing";
		ApiError apiError = new ApiError("EXC_002", BAD_REQUEST, ex.getMessage(), error);
		logger.error("Error Occured >>" + ex.getMessage());
		return buildResponseEntity(apiError);
	}

	// 3.
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {

		List<String> errors = new ArrayList<String>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}
		ApiError apiError = new ApiError("EXC_003", HttpStatus.BAD_REQUEST, "Duplicate Entry", errors);
		logger.error("Error Occured >>" + ex.getMessage());
		System.out.println(errors);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	// 4.
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {

		String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
		ApiError apiError = new ApiError("EXC_004", HttpStatus.BAD_REQUEST, ex.getMessage(), error);
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

	}

	// 5.
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
		ApiError apiError = new ApiError("EXC_005", HttpStatus.NOT_FOUND, ex.getMessage(), error);
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	// 6.
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

		ApiError apiError = new ApiError("EXC_006", HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), builder.toString());
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	// 7.
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

		ApiError apiError = new ApiError("EXC_007", HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(),
				builder.substring(0, builder.length() - 2));
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	// 8.

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		String error = "Malformed JSON request";
		ApiError apiError = new ApiError("EXC_008", HttpStatus.BAD_REQUEST, ex.getMessage(), error);
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	// 9.
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Error writing JSON output";
		ApiError apiError = new ApiError("EXC_009", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), error);
		logger.error("Error Occured >>" + ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	// 10.
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			WebRequest request) {
		ApiError apiError = null;
		if (ex.getCause() instanceof ConstraintViolationException) {
			apiError = new ApiError("EXC_0101", HttpStatus.CONFLICT, "Entry already present",ex.getMessage());
		} else {
			// apiError = new ApiError("EXC_0102", HttpStatus.INTERNAL_SERVER_ERROR,
			// ex.getMessage(), "Internal Server Error");
			apiError = new ApiError("EXC_0102", HttpStatus.INTERNAL_SERVER_ERROR, "Error in Saving Data.",
					"Internal Server Error");
		}
		logger.error("Error Occured >>" + ex.getMessage());

		return buildResponseEntity(apiError);
	}

	// User Defined
	// 11.
	// @ExceptionHandler(EntityNotFoundException.class)
	// protected ResponseEntity<Object>
	// handleEntityNotFound(javax.persistence.EntityNotFoundException ex) {
	// ApiError apiError = new ApiError("EXC_011", HttpStatus.NOT_FOUND,
	// ex.getMessage(), "Not Found");
	// logger.error("Error Occured >>" + ex.getMessage());
	// return buildResponseEntity(apiError);
	// }

	// 12.

	/*
	 * @ExceptionHandler(OAuth2Exception.class) protected ResponseEntity<Object>
	 * handleAuthError(OAuth2Exception ex) { ApiError apiError = new
	 * ApiError("EXC_012", HttpStatus.BAD_REQUEST, ex.getOAuth2ErrorCode(),
	 * "Error Occured >>" + ex.getMessage()); logger.error("Error Occured >>" +
	 * ex.getMessage()); return buildResponseEntity(apiError); }
	 */

	// 13.
	@ExceptionHandler(AuthenticationException.class)
	protected ResponseEntity<Object> handleAuthError(AuthenticationException ex) {
		ApiError apiError = new ApiError("EXC_013", HttpStatus.BAD_REQUEST, ex.getMessage(),
				"Error Occured >>" + ex.getMessage());
		logger.error("Error Occured >>" + ex.getMessage());
		return buildResponseEntity(apiError);
	}

	// 15.

	@ExceptionHandler(ResourceNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(ResourceNotFoundException ex) {
		ApiError apiError = new ApiError("EXC_0015", HttpStatus.NOT_FOUND, ex.getMessage(), "Not Found");
		logger.error("Error Occured >>" + ex.getMessage());
		return buildResponseEntity(apiError);
	}

	// Asyn exception
	protected org.springframework.http.ResponseEntity<Object> handleAsyncRequestTimeoutException(
			AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
		ApiError apiError = new ApiError("EXC_0016", status, ex.getMessage(), "Async Error occured");
		logger.error("Error Occured >>" + ex.getMessage());
		return buildResponseEntity(apiError);
	};

	// Rest
	@ExceptionHandler({ GenericException.class })
	public ResponseEntity<Object> handleGenericException(GenericException ex, WebRequest request) {
		ApiError apiError = new ApiError("GEN_0050", HttpStatus.EXPECTATION_FAILED, ex.getMessage(),
				"logical error occurred");
		logger.error("Error Occured >>" + ex.getMessage());
		return buildResponseEntity(apiError);
	}

	// 99.
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		String msg = ex.getMessage();
		HttpStatus sts = HttpStatus.INTERNAL_SERVER_ERROR;
		if (ex instanceof AccessDeniedException) {
			sts = HttpStatus.EXPECTATION_FAILED;
		}
		logger.error("Error Type" + ex.getClass().getCanonicalName());
		logger.error("Error Occured >>" + ex.getMessage());

		ApiError apiError = new ApiError("EXC_0099", sts, "Server Error Occured.",
				"Error occured of type " + ex.getClass().getCanonicalName());

		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	// ////////////////////////

	private String stackTraceData(StackTraceElement[] stacks) {
		StringBuilder str = new StringBuilder();

		for (StackTraceElement stackTraceElement : stacks) {
			str.append(stackTraceElement.toString()).append("\n");
		}
		return str.toString();

	}
}