package demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.camel.component.hystrix.metrics.servlet.HystrixEventStreamServlet;
import org.apache.qpid.jms.JmsConnectionFactory;


@SpringBootApplication
// @ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ServletRegistrationBean hystrixServletRegistrationBean() {
        ServletRegistrationBean mapping = new ServletRegistrationBean();
        mapping.setServlet(new HystrixEventStreamServlet());
        mapping.addUrlMappings("/hystrix.stream");
        mapping.setName("HystrixEventStreamServlet");

        return mapping;
    }


    @Value("${AMQP_REMOTE_URI}")
    String remoteUri;
    
    @Value("${AMQP_SERVICE_USERNAME}")
    String user;

    @Value("${AMQP_SERVICE_PASSWORD}")
    String pwd;

    @Bean
    public JmsConnectionFactory amqpConnectionFactory() {
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory();
        jmsConnectionFactory.setRemoteURI(remoteUri);
        jmsConnectionFactory.setUsername(user);
        jmsConnectionFactory.setPassword(pwd);
        return jmsConnectionFactory;
    }

    @Autowired JmsConnectionFactory amqpConnectionFactory;
    
    @Bean
    public org.apache.camel.component.amqp.AMQPComponent amqpConnection() {
        org.apache.camel.component.amqp.AMQPComponent amqp = new org.apache.camel.component.amqp.AMQPComponent();
        amqp.setConnectionFactory(amqpConnectionFactory);
        return amqp;
    }
}