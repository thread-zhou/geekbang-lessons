package org.geektimes.configuration.spi.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @ClassName: MicroprofileResourceConfigSource
 * @Description: {@link org.eclipse.microprofile.config.spi.ConfigSource} 的配置文件实现
 * @author: zhoujian
 * @date: 2021/3/20 13:35
 * @version: 1.0
 */
public class MicroprofileResourceConfigSource extends MapBasedConfigSource {

    private static final String configFileLocation = "META-INF/microprofile-config.properties";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public MicroprofileResourceConfigSource() {
        super("Default Config File", 100);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(configFileLocation);
        if (resource == null) {
            logger.info("The default config file can't be found in the classpath : " + configFileLocation);
            return;
        }
        try (InputStream inputStream = resource.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            configData.putAll(properties);
        }catch (IOException exception) {
            logger.log(Level.SEVERE, "The config file [ " + configFileLocation + " ] loading with an exception: [ " + exception.getMessage() + " ]");
        }
    }
}
