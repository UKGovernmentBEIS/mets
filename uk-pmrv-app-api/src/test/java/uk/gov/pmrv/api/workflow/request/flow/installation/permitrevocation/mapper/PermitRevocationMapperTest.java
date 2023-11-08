package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;

class PermitRevocationMapperTest {

	private PermitRevocationMapper mapper = Mappers.getMapper(PermitRevocationMapper.class);

    @Test
    void toApplicationSubmitRequestTaskPayload() {
    	PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
    			.paymentAmount(BigDecimal.TEN)
    			.build();
    	
    	PermitRevocationApplicationSubmitRequestTaskPayload result = mapper.toApplicationSubmitRequestTaskPayload(requestPayload);
    	
    	assertThat(result.getFeeAmount()).isEqualTo(BigDecimal.TEN);
    	assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD);
    }
}
