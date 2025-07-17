package uk.gov.pmrv.api.integration.common;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.api.restlogging.RestLoggingUtils;

@Log4j2
@Component
public class KafkaCorrelationParentHeaderProducerPmrvInterceptor<K, V>
		implements KafkaCorrelationParentHeaderProducerInterceptor<K, V> {

	@Override
	public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
		if (record.headers().lastHeader(KafkaConstants.CORRELATION_PARENT_ID_HEADER) == null) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (servletRequestAttributes != null) {
				HttpServletResponse response = servletRequestAttributes.getResponse();
				if (response != null) {
					record.headers().add(KafkaConstants.CORRELATION_PARENT_ID_HEADER,
							response.getHeader(RestLoggingUtils.CORRELATION_ID_HEADER).getBytes());
				}
			}
		}
		
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

	}

	@Override
	public void close() {

	}

	@Override
	public void configure(Map<String, ?> configs) {

	}
}