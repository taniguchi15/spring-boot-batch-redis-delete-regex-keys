package tech.taniguchi.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@EnableAutoConfiguration
@EnableBatchProcessing
@EnableConfigurationProperties
public class Main implements CommandLineRunner {

    final static Logger log = Logger.getLogger(Main.class.getName());

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(Main.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext applicationContext = application.run(args);
        SpringApplication.exit(applicationContext);

    }


    @Override
    public void run(String... args) throws Exception {
        log.info("---------- run start --------------");


        log.info("---------- run stop --------------");
    }

}
