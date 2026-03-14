package local.alejandrogb.metricsservers.exceptions.api;

public class ApiError {
	public String code;
	public String message;
	public String details;

	public ApiError(String code, String message, String details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}
}
