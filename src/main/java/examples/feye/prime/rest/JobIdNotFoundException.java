package examples.feye.prime.rest;

public class JobIdNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JobIdNotFoundException(String errorMsg) {
		super(errorMsg);
	}
}
