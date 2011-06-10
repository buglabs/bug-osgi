package javax.microedition.rms;

public class RecordStoreFullException extends RecordStoreException {
	private static final long serialVersionUID = 3455842059634140500L;
	
	public RecordStoreFullException(String message, Exception e) {
		super(message, e);
	}
}
