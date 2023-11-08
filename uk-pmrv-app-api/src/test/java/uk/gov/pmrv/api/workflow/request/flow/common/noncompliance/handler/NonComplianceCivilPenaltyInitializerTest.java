package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceCivilPenaltyInitializerTest {

    @InjectMocks
    private NonComplianceCivilPenaltyInitializer initializer;

    @Test
    void initializePayload_whenReissueCivilPenaltyIsFalse_thenCopyFromRequest() {

        final UUID civilPenalty = UUID.randomUUID();
        final Request request = Request.builder().payload(NonComplianceRequestPayload.builder()
            .reIssueCivilPenalty(false)
            .civilPenalty(civilPenalty).build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceCivilPenaltyRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD)
            .civilPenalty(civilPenalty)
            .build());
    }

    @Test
    void initializePayload_whenReissueCivilPenaltyIsTrue_thenFreshPayload() {

        final UUID civilPenalty = UUID.randomUUID();
        final Request request = Request.builder().payload(NonComplianceRequestPayload.builder()
            .reIssueCivilPenalty(true)
            .civilPenalty(civilPenalty).build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceCivilPenaltyRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD)
            .civilPenalty(null)
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.NON_COMPLIANCE_CIVIL_PENALTY,
            RequestTaskType.AVIATION_NON_COMPLIANCE_CIVIL_PENALTY);
    }

}
