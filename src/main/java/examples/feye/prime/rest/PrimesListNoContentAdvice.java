package examples.feye.prime.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * 
 * Advice to send Http 204 when the results are not ready
 *
 */
@ControllerAdvice
public class PrimesListNoContentAdvice {	
	@ResponseBody
	@ExceptionHandler(PrimesListNoContentException.class)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	String primesListNoContentHandler(PrimesListNoContentException ex) {
		return ex.getMessage();
	}}
