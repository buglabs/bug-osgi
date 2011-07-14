package com.buglabs.util.shell.pub;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;


/**
 * Default implementation of ShellSession.
 * 
 * @author kgilmer
 */
public class ShellSession implements IShellSession {
	private volatile boolean interrupt = false;
	private boolean disposed = false;
	/**
	 * This terminator string is used to determine when content from the output
	 * stream is completed for a given command.
	 */
	public static final String TERMINATOR = "234o987dsfkcqiuwey18837032843259d";
	public static final String LT = System.getProperty("line.separator");

	private Process process;

	private OutputStream pos = null;
	private static final String SHELL_PATH = "/bin/sh";
	private final File root;
	private final Writer out;

	/**
	 * @param root directory to run the shell session
	 * @param out where to direct output
	 * @throws IOException on File I/O error
	 */
	public ShellSession(File root, Writer out) throws IOException {
		this.root = root;

		if (out == null) {
			this.out = new NullWriter();
		} else {
			this.out = out;
		}

		initializeShell();
	}
	
	/**
	 * @param root base directory
	 * @throws IOException  on File I/O error
	 */
	public ShellSession(File root) throws IOException {
		this.root = root;
		this.out = new NullWriter();

		initializeShell();
	}

	/**
	 * @throws IOException  on File I/O error
	 */
	private void initializeShell() throws IOException {
		if (!disposed) {
			process = Runtime.getRuntime().exec(SHELL_PATH);
			pos = process.getOutputStream();

			if (root != null) {
				out.write(execute("cd " + root.getAbsolutePath()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shellservice.IShellService#execute(java.lang.String)
	 */
	synchronized public String execute(String command) throws IOException {
		if (disposed) {
			throw new IOException("This shell session has been disposed.");
		}
		
		String errorMessage = null;
		interrupt = false;
		out.write(command);

		sendToProcessAndTerminate(command);

		if (process.getErrorStream().available() > 0) {
			byte[] msg = new byte[process.getErrorStream().available()];

			process.getErrorStream().read(msg, 0, msg.length);
			out.write(new String(msg));
			errorMessage = "Error while executing: " + command + LT + new String(msg);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String line = null;

		while (((line = br.readLine()) != null) && !line.equals(TERMINATOR) && !interrupt) {
			sb.append(line);
			sb.append(LT);
			out.write(line);
			out.write(LT);
		}

		if (interrupt) {
			process.destroy();
			initializeShell();
			interrupt = false;
		}

		if (errorMessage != null) {
			throw new IOException(errorMessage);
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shellservice.IShellService#execute(java.lang.String,
	 * shell.pub.ICommandResponseHandler)
	 */
	public synchronized void execute(String command, ICommandResponseHandler handler) throws IOException {
		execute(command, TERMINATOR, handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shellservice.IShellService#execute(java.lang.String,
	 * java.lang.String, shell.pub.ICommandResponseHandler)
	 */
	public synchronized void execute(String command, String terminator, ICommandResponseHandler handler) throws IOException {
		if (disposed) {
			throw new IOException("This shell session has been disposed.");
		}
		
		interrupt = false;
		sendToProcessAndTerminate(command);

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		InputStream errIs = process.getErrorStream();
		String std = null;

		do {
			if (errIs.available() > 0) {
				byte[] msg = new byte[errIs.available()];

				errIs.read(msg, 0, msg.length);
				out.write(new String(msg));
				handler.response(new String(msg), true);
			}

			std = br.readLine();

			if (std != null && !std.equals(terminator)) {
				out.write(std);
				handler.response(std, false);
			}

		} while (std != null && !std.equals(terminator) && !interrupt);

		if (interrupt) {
			process.destroy();
			initializeShell();
			interrupt = false;
		}
	}

	/**
	 * Send command string to shell process and add special terminator string so
	 * reader knows when output is complete.
	 * 
	 * @param command command to execute
	 * @throws IOException  on File I/O error
	 */
	private void sendToProcessAndTerminate(String command) throws IOException {
		pos.write(command.getBytes());
		pos.write(LT.getBytes());
		pos.write("echo ".getBytes());
		pos.write(TERMINATOR.getBytes());
		pos.write(LT.getBytes());
		pos.flush();
	}

	/* (non-Javadoc)
	 * @see com.buglabs.util.shell.pub.IShellSession#interrupt()
	 */
	public void interrupt() {
		interrupt = true;
	}

	/* (non-Javadoc)
	 * @see com.buglabs.util.shell.pub.IShellSession#dispose()
	 */
	public void dispose() {
		this.disposed = true;
		interrupt();
	}

	/**
	 * A Writer that does nothing.
	 * 
	 * @author kgilmer
	 */
	private class NullWriter extends Writer {
		/* (non-Javadoc)
		 * @see java.io.Writer#close()
		 */
		public void close() throws IOException {
		}

		/* (non-Javadoc)
		 * @see java.io.Writer#flush()
		 */
		public void flush() throws IOException {
		}

		/* (non-Javadoc)
		 * @see java.io.Writer#write(char[], int, int)
		 */
		public void write(char[] cbuf, int off, int len) throws IOException {
		}
	}
}
