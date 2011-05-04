package webadmin;

import java.util.ArrayList;

/**
 * Maintains buffer content between controllers 
 * @author bballantine
 *
 */
public class BufferManager {
	private static BufferManager _instance;
	
	private ArrayList buffer = new ArrayList();
	
	public static BufferManager getInstance() {
		if(_instance == null) {
			synchronized(BufferManager.class) {
				if(_instance == null) {
					_instance = new BufferManager();
				}
			}
		}
		return _instance;
	}
	
	public BufferManager() {}
	
	public void updateBuffer(String newstuff) {
		synchronized(buffer) {
			buffer.add(newstuff);
		}
	}
	
	public ArrayList getBuffer() {
		synchronized(buffer) {
			ArrayList output = new ArrayList(buffer);
			buffer.clear();
			return output;
		}
	}
}
