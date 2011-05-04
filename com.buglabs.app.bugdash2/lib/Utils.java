package lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import com.buglabs.app.bugdash2.WebAdminSettings;


/**
 * Network utilities -- provided by jconnolly 
 * This needs cleanup..
 */
public class Utils {
	private static final int POLL_DELAY = 100;
	/**
	  * Attempts to connect to a service at the specified address
	  * and port, for a specified maximum amount of time.
	  *
	  *	@param	url	    url of file
	  *	@param	dest	local destination of file
	  * @param	md5		the md5 string for verification
	  * @throws IOException 
	  */
	public static synchronized boolean downloadAndCheckMD5(URL url, File dest, String md5) throws IOException{
		// create /root if doesn't exist
		dest.getParentFile().mkdirs();
		BufferedInputStream is = new BufferedInputStream(url.openStream());
		FileOutputStream fos = new FileOutputStream(dest);
		byte buffer[] = new byte[8094];
		int read;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
				fos.write(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < md5sum.length; i++) {
				hexString.append(Integer.toHexString(0xFF & md5sum[i]));
			}
			fos.close();
			is.close();
			String s = new String(hexString);
			return s.equalsIgnoreCase(md5);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	  * Attempts to connect to a service at the specified address
	  * and port, for a specified maximum amount of time.
	  * 
	  * Taken from http://java.sun.com/developer/technicalArticles/Networking/timeouts/
	  *
	  *	@param	addr	Address of host
	  *	@param	port	Port of service
	  * @param	delay	Delay in milliseconds
	  * @throws IOException 
	  */
	public static boolean checkInternetConnection(InetAddress addr, int port, int delay) throws IOException{
			// Create a new socket thread, and start it running
			SocketThread st = new SocketThread( addr, port );
			st.start();

			int timer = 0;
			Socket sock = null;

			for (;;)
			{
				// Check to see if a connection is established
				if (st.isConnected())
				{
					// Yes ...  assign to sock variable, and break out of loop
					sock = st.getSocket();
					break;
				}
				else
				{
					// Check to see if an error occurred
					if (st.isError())
					{
						// No connection could be established
						throw (st.getException());
					}

					try
					{
						// Sleep for a short period of time
						Thread.sleep ( POLL_DELAY );
					}
					catch (InterruptedException ie) {}

					// Increment timer
					timer += POLL_DELAY;

					// Check to see if time limit exceeded
					if (timer > delay)
					{
						return false;
						
					}
				}
			}
			st = null;
			return true;
		}
	static class SocketThread extends Thread
	{
		// Socket connection to remote host
		volatile private Socket m_connection = null;
		// Hostname to connect to
		private String m_host       = null;
		// Internet Address to connect to
		private InetAddress m_inet  = null;
		// Port number to connect to
		private int    m_port       = 0;
		// Exception in the event a connection error occurs
		private IOException m_exception = null;

		// Connect to the specified host and port number
		public SocketThread ( String host, int port)
		{
			// Assign to member variables
			m_host = host;
			m_port = port;
		}

		// Connect to the specified host IP and port number
		public SocketThread ( InetAddress inetAddr, int port )
		{
			// Assign to member variables
			m_inet = inetAddr;
			m_port = port;
		}

		public void run()
		{
			// Socket used for establishing a connection
			Socket sock = null;

			try
			{
				// Was a string or an inet specified
				if (m_host != null)
				{
					// Connect to a remote host - BLOCKING I/O
					sock = new Socket (m_host, m_port);
				}
				else
				{
					// Connect to a remote host - BLOCKING I/O
					sock = new Socket (m_inet, m_port);
				}
			}
			catch (IOException ioe)
			{
				// Assign to our exception member variable
				m_exception = ioe;
				return;
			}

			// If socket constructor returned without error,
			// then connection finished
			m_connection = sock;
		}

		// Are we connected?
		public boolean isConnected()
		{
			if (m_connection == null)
				return false;
			else
				return true;
		}

		// Did an error occur?
		public boolean isError()
		{
			if (m_exception == null)
				return false;
			else
				return true;
		}

		// Get socket
		public Socket getSocket()
		{
			return m_connection;
		}

		// Get exception
		public IOException getException()
		{
			return m_exception;
		}
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(getMacAddress());
	}
	
	public final static String getMacAddress() throws IOException {
		try {
			return linuxParseMacAddress(linuxRunIfConfigCommand());
		} catch (ParseException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			throw new ParseException(ex.getMessage(), 0);
		}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;
			// see if line contains IP address
			if (containsLocalHost && lastMacAddress != null) {
				return lastMacAddress;
			}
			// see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition <= 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
			if (linuxIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}
		
		//We didn't get the hw address of InetAddress.getLocalHost().getHostAddress() but let's return the last good hw address.
		if (lastMacAddress != null) {
			return lastMacAddress;
		}

		ParseException ex = new ParseException("cannot read MAC address for "
				+ localHost + " from [" + ipConfigResponse + "]", 0);
		throw ex;
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate) {
		// TODO: use a smart regular expression
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	private final static String linuxRunIfConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}	
	
	public static String getIPAddress() throws SocketException {
		String ip_address = ""; 
		Enumeration networkInterfaces, addresses;
		InetAddress adrs; 
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
				// TODO: is this necessary? 
				if (!ni.getDisplayName().equals("lo") && !ni.getDisplayName().equals("usb0")) {
					addresses = ni.getInetAddresses();
					while (addresses.hasMoreElements()) {
						adrs = (InetAddress)addresses.nextElement();
						if (adrs.isSiteLocalAddress()) {
							ip_address = adrs.getHostAddress();
						}
					}
				}
			}
			
		} catch (SocketException e) {
			throw new SocketException(); 
		}
		networkInterfaces = null; 
		addresses = null; 

		return ip_address; 
	}
	public static String readCookie(Cookie[] cookies, String name) {
		String value = ""; 
		Cookie cookie;
		// is there a better way to read cookie? 
		for (int i=0; i < cookies.length; i++) {
			cookie = cookies[i];
			if (cookie.getName().equals(name)) {
				value = cookie.getValue(); break; 
			}
		}		
		return value; 
	}
	
	public static String sessionIdGenerator()
	{
		String result = "";
		Random rand = new Random();
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for(int i = 0; i < 10; i++)
		{
			result += alphabet.charAt(rand.nextInt(26));
		}
		result += "-";
		result += System.currentTimeMillis();
		return result;
	}
}
