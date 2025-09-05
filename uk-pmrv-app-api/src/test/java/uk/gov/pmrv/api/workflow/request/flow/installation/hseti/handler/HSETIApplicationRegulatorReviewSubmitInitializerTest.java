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
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationRegulatorReviewSubmitInitializerTest {

    @InjectMocks
    private HSETIApplicationRegulatorReviewSubmitInitializer initializer;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "HSETI00001-2021_2025";

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.HSE_TI)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(HSETIRequestPayload
                         .builder()
                         .hseti(HSETI.builder()
                                 .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                                 .build())
                         .build()
                 )
                .metadata(HSETIRequestMetadata.builder()
                        .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                        .build())
                 .build();

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .hseti( ((HSETIRequestPayload) request.getPayload()).getHseti())
                .hsetiAttachments( ((HSETIRequestPayload) request.getPayload()).getHsetiAttachments())
                .hsetiSectionsCompleted( ((HSETIRequestPayload) request.getPayload()).getHsetiSectionsCompleted())
                .build();


        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);

    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
