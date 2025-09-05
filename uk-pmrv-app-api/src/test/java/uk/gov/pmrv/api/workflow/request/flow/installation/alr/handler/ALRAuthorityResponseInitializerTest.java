package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ALRAuthorityResponseInitializerTest {

    @InjectMocks
    private ALRAuthorityResponseInitializer initializer;

    @Test
    void initializePayload() {
        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build()
        );
        final Request request = Request.builder()
                .type(RequestType.DOAL)
                .payload(ALRRequestPayload.builder()
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().allocations(allocations)
                                .build())
                        .build())
                .build();

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertEquals(requestTaskPayload, ALRAuthorityResponseSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD)
                .regulatorPreliminaryAllocations(new HashSet<>(allocations))
                .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().build())
                .alrFileVersion(1)
                .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.ALR_AUTHORITY_RESPONSE_SUBMIT);
    }
}
