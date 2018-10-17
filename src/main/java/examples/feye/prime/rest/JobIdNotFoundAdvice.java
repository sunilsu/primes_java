package examples.feye.prime.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * 
 * Advice to send Http 404 on JobNotFOundException
 *
 */
@ControllerAdvice
public class JobIdNotFoundAdvice {	
	@ResponseBody
	@ExceptionHandler(JobIdNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String jobIdNotFoundHandler(JobIdNotFoundException ex) {
		return ex.getMessage();
	}}
