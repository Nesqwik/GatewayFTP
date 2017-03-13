package car.tp2.utils;

public class FtpRequestException extends Exception {
	private static final long serialVersionUID = 9037936674545495024L;
	private int code;
	private String data;
	
	public FtpRequestException(final int code) { 
		this.code = code;
	}
	
	public FtpRequestException(final int code, final String data) { 
		this.code = code;
		this.setData(data);
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final int code) {
		this.code = code;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
}
