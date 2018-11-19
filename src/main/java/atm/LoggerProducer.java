package atm;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {
    
    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        Class<?> memberClass = injectionPoint.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(memberClass); // configuration will be taken from jboss logging configuration
    }
    
}
