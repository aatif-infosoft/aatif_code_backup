package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {
	public static Properties props;

	static {
		props = new Properties();
		try {
			PropertiesUtils util = new PropertiesUtils();
			System.out.println("PROPERTIES UTILS CALLING");
			props = util.getPropertiesFromClasspath("/alerts.properties");
			System.out.println("CALLD");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static Set<Object> getkeys() {
		return props.keySet();
	}

	public Properties getPropertiesFromClasspath(String propFileName)
			throws IOException {
		Properties props = new Properties();
		/*
		 * InputStream inputStream = this.getClass().getClassLoader()
		 * .getResourceAsStream(propFileName); if (inputStream == null) { throw
		 * new FileNotFoundException("property file '" + propFileName +
		 * "' not found in the classpath"); }
		 */
		props.load(getClass().getResourceAsStream(propFileName));
		return props;
	}
}
