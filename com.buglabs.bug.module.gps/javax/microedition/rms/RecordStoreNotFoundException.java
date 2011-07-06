package javax.microedition.rms;


public class RecordStoreNotFoundException extends RecordStoreException {
	private static final long serialVersionUID = -1377142334003604307L;

	public RecordStoreNotFoundException(String message) {
		super(message);
	}

	public RecordStoreNotFoundException(String message, Exception e) {
		super(message, e);
	}

}
