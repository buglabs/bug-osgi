package com.buglabs.osgi.log;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

/**
 * This class implements both the LogService and LogReaderServices.
 * 
 * @author kgilmer
 *
 */
public class LogServiceImpl implements LogService, LogReaderService {
	/**
	 * Number of messages to store before removing the oldest.
	 */
	private static final int DEFAULT_LOG_MESSAGE_BUFFER = 32;
	private static final String DEFAULT_DATE_FORMAT = "MM/dd HH:mm.ss";
	
	/**
	 * In-memory storage for log messages.
	 */
	private FixedSizeQueue queue;
	
	/**
	 * If true, no log data should be emitted.
	 */
	public boolean quiet;

	/**
	 * Log level that should emit data.
	 */
	public int logLevel;
	public static PrintStream out;
	public static PrintStream err;
	public int bufferSize;


	private List listeners;
	private String dateFormat;
	
	public LogServiceImpl(BundleContext bundleContext) {
		quiet = bundleContext.getProperty("org.osgi.logging.quiet") != null;
		if (bundleContext.getProperty("org.osgi.logging.level") != null) {
			logLevel = Integer.parseInt(bundleContext.getProperty("org.osgi.logging.level"));
		} else {
			// By default print all log data.
			logLevel = LogService.LOG_DEBUG;
		}

		// Use the system io stream. This could be augmented in the future to
		// send to files or servers.
		out = System.out;
		err = System.err;
		if (bundleContext.getProperty("org.osgi.logging.bufferSize") != null) {
			bufferSize = Integer.parseInt(bundleContext.getProperty("org.osgi.logging.bufferSize"));
		} else {
			bufferSize = DEFAULT_LOG_MESSAGE_BUFFER;
		}
		queue = new FixedSizeQueue(bufferSize);
		
		if (bundleContext.getProperty("org.osgi.logging.date.format") != null) {
			dateFormat = bundleContext.getProperty("org.osgi.logging.bufferSize");
		} else {
			dateFormat = DEFAULT_DATE_FORMAT;
		}
	}
	
	public void dispose() {
		if (listeners != null) {
			listeners.clear();
		}
		
		if (queue != null) {
			queue.clear();
		}
	}

	
	public void log(int level, String message) {
		LogEntry le = new LogEntryImpl(null, level, message, null, System.currentTimeMillis(), null, dateFormat);
		printLog(le);
		queue.add(le);
		notifyListeners(le);
	}

	private void printLog(LogEntry le) {
		if (!quiet && isPrintable(le.getLevel())) {
			if (le.getLevel() == LogService.LOG_ERROR) {
				((LogEntryImpl) le).print(err);
			} else {
				((LogEntryImpl) le).print(out);
			}
		}
	}

	/**
	 * Should log content for given level be printed?
	 * 
	 * @param level
	 * @return
	 */
	private boolean isPrintable(int level) {
		return level <= logLevel;
	}

	
	public void log(int arg0, String arg1, Throwable arg2) {
		LogEntry le = new LogEntryImpl(null, arg0, arg1, null, System.currentTimeMillis(), arg2, dateFormat);
		printLog(le);
		queue.add(le);
		notifyListeners(le);
	}

	
	public void log(ServiceReference arg0, int arg1, String arg2) {
		LogEntry le = new LogEntryImpl(null, arg1, arg2, arg0, System.currentTimeMillis(), null, dateFormat);
		printLog(le);
		queue.add(le);
		notifyListeners(le);
	}

	
	public void log(ServiceReference arg0, int arg1, String arg2, Throwable arg3) {
		LogEntry le = new LogEntryImpl(null, arg1, arg2, arg0, System.currentTimeMillis(), arg3, dateFormat);
		printLog(le);
		queue.add(le);
		notifyListeners(le);
	}

	private void notifyListeners(LogEntry le) {
		if (listeners == null || listeners.size() == 0) {
			return;
		}

		synchronized (listeners) {
			for (Iterator i = listeners.iterator(); i.hasNext();) {
				((LogListener) i.next()).logged(le);
			}
		}
	}
	
	public void addLogListener(LogListener arg0) {
		if (listeners == null) {
			listeners = new ArrayList();
		}

		synchronized (listeners) {
			if (!listeners.contains(arg0)) {
				listeners.add(arg0);
			}
		}
	}

	
	public Enumeration getLog() {
		//To prevent concurrent modification, create a copy of the log and pass it to the client.
		final List l = new ArrayList(queue);
		
		return new Enumeration() {
			Iterator i = l.iterator();

			
			public boolean hasMoreElements() {
				return i.hasNext();
			}

			
			public Object nextElement() {
				return i.next();
			}
		};
	}

	
	public void removeLogListener(LogListener arg0) {
		if (listeners != null) {
			synchronized (listeners) {
				listeners.remove(arg0);
			}
		}
	}
}
