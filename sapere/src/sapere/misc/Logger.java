package sapere.misc;

import java.util.prefs.Preferences;

public class Logger {
	private static Logger instance;
	private Preferences prefs;
	
	private Logger(){
		prefs = Preferences.systemRoot();
	}
	
	public static Logger getInstance(){
		if(instance == null)
			instance = new Logger();
		return instance;
	}
	
	public void log(String s){
		if(prefs.getBoolean("VERBOSE",false))
			System.out.println(s);
	}
	
	public void log(String s, int logLevel){
		if(prefs.getBoolean("VERBOSE",false) && prefs.getInt("LOG_LEVEL", 0)>=logLevel)
			System.out.println(s);
	}
}
