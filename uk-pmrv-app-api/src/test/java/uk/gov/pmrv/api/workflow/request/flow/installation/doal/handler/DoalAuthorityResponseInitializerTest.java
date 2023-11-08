package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.time.Year;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DoalAuthorityResponseInitializerTest {

    @InjectMocks
    private DoalAuthorityResponseInitializer initializer;

    @Test
    void initializePayload() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build()
        );
        final Request request = Request.builder()
                .type(RequestType.DOAL)
                .payload(DoalRequestPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>(allocations))
                                        .build())
                                .build())
                        .build())
                .build();

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertEquals(requestTaskPayload, DoalAuthorityResponseRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_AUTHORITY_RESPONSE_PAYLOAD)
                .regulatorPreliminaryAllocations(new TreeSet<>(allocations))
                .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.DOAL_AUTHORITY_RESPONSE);
    }
}
