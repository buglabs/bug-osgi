package com.buglabs.common.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import com.buglabs.services.ws.IWSResponse;
import com.buglabs.services.ws.PublicWSDefinition;
import com.buglabs.services.ws.PublicWSProvider;
import com.buglabs.services.ws.WSResponse;

import junit.framework.TestCase;

public class TestPublicWSProvider extends TestCase {

	private String port;
	private BundleContext context;

	protected void setUp() throws Exception {
		port = System.getProperty("org.osgi.service.http.port");
		context = Activator.getDefault().getContext();
	}

	public void testPublicWSProviderGET() throws InvalidSyntaxException, IOException {
		assertNotNull(port);

		ServiceTracker wsST = null; //PublicWSAdminTracker.createTracker(context, new NameWS());

		URL url = new URL("http://localhost:" + port + "/service/Name");

		byte[] content = get(url);

		assertEquals("Test",new String(content));

		wsST.close();
	}
	
	public void testPublicWSProviderPUTGET() throws InvalidSyntaxException, IOException {
		String name = "ModdedName";
		
		assertNotNull(port);

		ServiceTracker wsST = null; //PublicWSAdminTracker.createTracker(context, new NameWS());

		URL url = new URL("http://localhost:" + port + "/service/Name");
		post(url, name);
		
		byte[] content = get(url);
		assertEquals(name, new String(content));

		wsST.close();
	}

	private byte[] get(URL url) throws IOException {
		InputStream is = url.openStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buff = new byte[1024];
		int read = -1;

		do {
			read = is.read(buff);

			if(read != -1) {
				baos.write(buff, 0, read);
			}

		} while (read != -1);

		return baos.toByteArray();
	}

	private void post(URL url, String data) throws IOException {
		post(url, new ByteArrayInputStream(data.getBytes()));
	}
	
	private void post(URL url, InputStream is) throws IOException {
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		conn.connect();
		
		pipe(is, conn.getOutputStream());
	}
	
	private void pipe(InputStream is, OutputStream os) throws IOException {
		byte[] tmp = new byte[1024];
		int read = -1;
		
		do {
			read = is.read(tmp);
			if(read != -1) {
				os.write(tmp, 0, read);
			}
		} while(read != -1);
	}
	
	private class NameWS implements PublicWSProvider {
		
		private volatile String name = "Test";
		
		public PublicWSDefinition discover(int operation) {

			switch(operation) {
			case PublicWSProvider.GET:
				return new PublicWSDefinition() {

					public List getParameters() {
						return null;
					}

					public String getReturnType() {
						return "text/plain";
					}
				};
			
			case PublicWSProvider.POST:
				return null;
			}

			return null;
		}

		public IWSResponse execute(int operation, String input) {
			
			switch(operation) {
			case PublicWSProvider.GET:
				return new WSResponse(name, "text/plain");
			
			case PublicWSProvider.POST:
				name = input;
				return null;
			} 
			
			return null;
		}

		public String getDescription() {
			return "Allows the user to get / post a name";
		}

		public String getPublicName() {
			return "Name";
		}
	}
}
