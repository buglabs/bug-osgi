package javax.microedition.rms;

public class RecordStoreNotOpenException extends RecordStoreException {
	private static final long serialVersionUID = 9201597377208782520L;

	public RecordStoreNotOpenException(String message, Exception e) {
		super(message, e);
	}
}
