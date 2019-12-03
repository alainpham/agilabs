package agilabs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.apache.camel.component.hystrix.metrics.servlet.HystrixEventStreamServlet;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



	@Bean
	public ServletRegistrationBean metricsServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixEventStreamServlet(), "/hystrix.stream");
		return registration;
	}

}