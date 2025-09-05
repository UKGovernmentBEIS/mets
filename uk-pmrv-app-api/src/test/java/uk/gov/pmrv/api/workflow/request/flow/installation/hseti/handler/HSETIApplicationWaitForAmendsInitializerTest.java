package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIAllocationPeriod;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationWaitForAmendsInitializerTest {

    @InjectMocks
    private HSETIApplicationWaitForAmendsInitializer initializer;


    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "ASD";

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.HSE_TI)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(HSETIRequestPayload
                         .builder()
                         .hseti(HSETI
                                 .builder()
                                 .notes("test")
                                 .build())
                         .build()
                 )
                 .metadata(HSETIRequestMetadata.builder().allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025).build())
                 .build();


        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.HSE_TI_WAIT_FOR_AMENDS_PAYLOAD)
                .hseti( ((HSETIRequestPayload) request.getPayload()).getHseti())
                .build();


        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.HSE_TI_WAIT_FOR_AMENDS);
    }
}
