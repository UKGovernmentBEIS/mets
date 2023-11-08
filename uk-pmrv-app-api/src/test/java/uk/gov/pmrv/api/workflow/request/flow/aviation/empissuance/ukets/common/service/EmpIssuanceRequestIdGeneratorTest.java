package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceRequestIdGeneratorTest {

    @InjectMocks
    private EmpIssuanceRequestIdGenerator requestIdGenerator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder().accountId(1345L).build();

        String requestId = requestIdGenerator.generate(params);

        assertThat(requestId).isEqualTo("EMP01345");
    }

    @Test
    void getTypes() {
        assertThat(requestIdGenerator.getTypes()).containsExactly(RequestType.EMP_ISSUANCE_UKETS, RequestType.EMP_ISSUANCE_CORSIA);
    }

    @Test
    void getPrefix() {
        assertEquals("EMP", requestIdGenerator.getPrefix());
    }
}