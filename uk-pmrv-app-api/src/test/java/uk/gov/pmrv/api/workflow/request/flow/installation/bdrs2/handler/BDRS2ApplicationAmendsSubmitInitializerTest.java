package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private BDRS2ApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {
        Long accountId = 1L;
        String requestId = "BDRS2-00001-2025";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(BDRS2RequestPayload
                        .builder()
                        .bdrs2(BDRS2
                                .builder()
                                .build())
                        .build()
                )
                .metadata(BDRS2RequestMetadata.builder().year(Year.of(2025)).build())
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .bdrs2(((BDRS2RequestPayload) request.getPayload()).getBdrs2())
                .bdrs2Attachments(((BDRS2RequestPayload) request.getPayload()).getBdrs2Attachments())
                .bdrs2SectionsCompleted(((BDRS2RequestPayload) request.getPayload()).getBdrs2SectionsCompleted())
                .bdrs2FileVersion(((BDRS2RequestPayload) request.getPayload()).getBdrs2FileVersion())
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload actualTaskPayload =
                (BDRS2ApplicationAmendsSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDRS2_APPLICATION_AMENDS_SUBMIT);
    }
}
