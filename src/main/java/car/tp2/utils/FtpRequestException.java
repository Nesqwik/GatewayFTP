package car.tp2.utils;

/**
 * 
 * @author Louis GUILBERT et Jonathan LECOINTE
 *
 * FtpRequestException : 
 * Exception en cas d'erreur lors d'une action ftp
 */
public class FtpRequestException extends Exception {
	private static final long serialVersionUID = 9037936674545495024L;
	private int code;
	private String data;
	
	/**
	 * Constructeur avec le code d'erreur
	 * @param code le code d'erreur
	 */
	public FtpRequestException(final int code) { 
		this.code = code;
	}
	
	/**
	 * Constructeur avec le code d'erreur et une donnée
	 * @param code code d'erreur 
	 * @param data une donnée
	 */
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
	public void setData(final String data) {
		this.data = data;
	}
}
