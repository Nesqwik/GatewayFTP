package car.tp2;

public class HttpResponse<T> {
	private int code;
	private T data;
	
	public HttpResponse(int code) {
		this.code = code;
	}
	
	public HttpResponse(int code, T data) {
		this.code = code;
		this.data = data;
	}

	public Object getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
