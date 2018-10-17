package examples.feye.prime.rest;

public class PrimesListNoContentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PrimesListNoContentException(String errorMsg) {
		super(errorMsg);
	}
}
