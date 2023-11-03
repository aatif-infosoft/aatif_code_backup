package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperties {
	
	String propFileName;
	
	public GetProperties(String propFileName) {
		this.propFileName = propFileName;
	}
	
	public  Properties getPropertiesFromClasspath(String propFileName)
			throws IOException {
		Properties props = new Properties();

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		props.load(inputStream);
		return props;
	}

}
