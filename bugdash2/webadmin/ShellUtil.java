package webadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.buglabs.bug.base.pub.IShellService;
import com.buglabs.bug.base.pub.ICommandResponseHandler;
import com.buglabs.bug.base.pub.IShellSession;

import freemarker.template.SimpleScalar;

/**
 * This class uses ShellService to execute a command and returns its output in various formats. 
 * @author akweon
 *
 */
public class ShellUtil {
	private static IShellSession session = null;
	
	public static SimpleScalar getSimpleScalar(String cmd) {
		SimpleScalar result = new SimpleScalar(""); 
		TemplateCommandHandler handler = new TemplateCommandHandler(result); 
		return (SimpleScalar)execute(cmd, result, handler);  
	}
	
	public static List getList(String cmd) {
		List result = new ArrayList(); 
		ListCommandHandler handler = new ListCommandHandler(result); 
		return (List)execute(cmd, result, handler); 
	}	
	
	public static Object execute(String cmd, Object result, ICommandResponseHandler handler) {
		IShellSession session = getSession();
		if (!cmd.equals("")) {
			LogManager.logDebug("ShellUtil cmd: " + cmd);
			try {
				session.execute(cmd, handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;				
	}
	
	public static IShellSession getSession() {
		if (session == null) {
			synchronized (IShellSession.class) {
				if (session == null) {
					IShellService shell = ShellManager.getShell();
					try {
						session = shell.createShellSession();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return session;
	}	
	
	public static void destroySession() {
		if (session != null) {
			session.dispose(); 
			session = null; 
		}
	}
	/*
	 * don't allow commands with &-- we don't let users enter commands at the moment  
	public static String cleanCommand(String cmd) {
		String result = cmd; 
		if (result.indexOf("&") > 0) {
			result = result.substring(0, result.indexOf("&")-1);
		}
		return result; 
	}*/
}
