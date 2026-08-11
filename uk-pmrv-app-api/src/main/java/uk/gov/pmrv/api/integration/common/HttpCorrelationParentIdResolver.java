package uk.gov.pmrv.api.integration.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.netz.api.kafka.correlation.KafkaCorrelationParentIdResolver;
import uk.gov.netz.api.restlogging.RestLoggingUtils;

@Component
public class HttpCorrelationParentIdResolver implements KafkaCorrelationParentIdResolver {

    @Override
    public String resolveParentCorrelationId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletResponse response = attrs.getResponse();
        return response != null ? response.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER) : null;
    }
}
