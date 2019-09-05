package poc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.apache.qpid.jms.JmsConnectionFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Value("${AMQP_REMOTE_URI}")
    String remoteUri;
    
    @Value("${AMQP_SERVICE_USERNAME}")
    String user;

    @Value("${AMQP_SERVICE_PASSWORD}")
    String pwd;

    @Bean
    public org.apache.qpid.jms.JmsConnectionFactory amqpConnectionFactory() {
        org.apache.qpid.jms.JmsConnectionFactory jmsConnectionFactory = new org.apache.qpid.jms.JmsConnectionFactory();
        // jmsConnectionFactory.setRemoteURI("amqps://messaging-r0uwkvz700-amq-online-infra.apps.88.198.65.4.nip.io:443?transport.trustAll=true");
        // jmsConnectionFactory.setUsername("user1");
        // jmsConnectionFactory.setPassword("password");
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