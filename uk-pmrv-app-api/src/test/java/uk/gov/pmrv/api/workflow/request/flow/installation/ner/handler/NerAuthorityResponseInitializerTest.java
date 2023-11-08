package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

@ExtendWith(MockitoExtension.class)
class NerAuthorityResponseInitializerTest {

    @InjectMocks
    private NerAuthorityResponseInitializer initializer;

    @Test
    void initializePayload() {

        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
            PreliminaryAllocation.builder()
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .year(Year.of(2022))
                .build()
        );
        final Request request = Request.builder().payload(NerRequestPayload.builder()
            .determination(NerProceedToAuthorityDetermination.builder()
                .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                .build()).build()).build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NerAuthorityResponseRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_AUTHORITY_RESPONSE_PAYLOAD)
            .originalPreliminaryAllocations(new TreeSet<>(preliminaryAllocations))
            .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.NER_AUTHORITY_RESPONSE);
    }
}
