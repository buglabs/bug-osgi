package javax.microedition.rms;


public class RecordStoreException extends Exception {
	private static final long serialVersionUID = -6378491505725083773L;

	public RecordStoreException(String message) {
		super(message);
	}

	public RecordStoreException(String message, Exception e) {
		super(message, e);
	}
}
