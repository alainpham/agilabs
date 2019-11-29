package itteddy;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.camel.main.Main;
import org.slf4j.LoggerFactory;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;

import org.slf4j.Logger;

public class MainApp {

    private static Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String... args) throws Exception {
        
        // loading properties files
        properties = new Properties();

        File propFile = new File("config/application.properties");
        if (propFile.exists()){
            FileReader fr = new FileReader(propFile);
            properties.load(fr);
            fr.close();
            logger.info("properties used from config/application.properties");
        }else {
            properties.load(MainApp.class.getResourceAsStream("/application.properties"));
            logger.info("properties used from classpath");
        }

        Main main = new Main();
        main.setPropertyPlaceholderLocations("classpath:/application.properties,file:config/application.properties;optional=true");
        main.bind("fileUtils", new FileUtils());
        main.addRouteBuilder(new AppRouteBuilder());
        main.run(args);
    }

}

