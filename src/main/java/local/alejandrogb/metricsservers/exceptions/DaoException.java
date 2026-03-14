package local.alejandrogb.metricsservers.exceptions;

public class DaoException extends RuntimeException {
	private static final long serialVersionUID = -5233773572602699648L;

	public DaoException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
