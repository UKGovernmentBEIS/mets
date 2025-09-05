package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceReason;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class NonComplianceFinalDeterminationInitializerTest {

    @InjectMocks
    private NonComplianceFinalDeterminationInitializer initializer;

    @Test
    void initializePayload() {

        final LocalDate nonComplianceDate = LocalDate.of(2024, 9, 1);
        final LocalDate complianceDate = LocalDate.of(2024, 10, 1);

        final Request request = Request.builder()
            .payload(NonComplianceRequestPayload
                    .builder()
                    .reason(NonComplianceReason.EXCEEDING_EMISSIONS_TARGET)
                    .complianceDate(complianceDate)
                    .nonComplianceDate(nonComplianceDate)
                    .comments("test comments")
                    .build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceFinalDeterminationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD)
            .reason(NonComplianceReason.EXCEEDING_EMISSIONS_TARGET)
            .complianceDate(complianceDate)
            .nonComplianceDate(nonComplianceDate)
            .nonComplianceComments("test comments")
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.NON_COMPLIANCE_FINAL_DETERMINATION,
            RequestTaskType.AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION);
    }

}
