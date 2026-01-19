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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationSubmitInitializerTest {

    @InjectMocks
    private BDRS2ApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "BDRS2-00001-2025";

        BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .applicationWithdrawalReason(null)
                .covidAdjustments(true)
                .inEiteSector(true)
                .build();

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
                                .bdrs2guardQuestions(guardQuestions)
                                .build())
                        .build()
                )
                .metadata(BDRS2RequestMetadata.builder().year(Year.of(2025)).build())
                .build();

        BDRS2ApplicationSubmitRequestTaskPayload taskPayload = BDRS2ApplicationSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_SUBMIT_PAYLOAD)
                .bdrs2( ((BDRS2RequestPayload) request.getPayload()).getBdrs2())
                .bdrs2Attachments( ((BDRS2RequestPayload) request.getPayload()).getBdrs2Attachments())
                .bdrs2SectionsCompleted( ((BDRS2RequestPayload) request.getPayload()).getBdrs2SectionsCompleted())
                .bdrs2FileVersion(1)
                .build();

        BDRS2ApplicationSubmitRequestTaskPayload actualTaskPayload = (BDRS2ApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDRS2_APPLICATION_SUBMIT);
    }
}