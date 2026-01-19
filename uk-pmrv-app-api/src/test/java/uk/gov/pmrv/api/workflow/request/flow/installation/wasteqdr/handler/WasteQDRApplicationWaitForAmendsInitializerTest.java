package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WasteQDRApplicationWaitForAmendsInitializerTest {

    @InjectMocks
    private WasteQDRApplicationWaitForAmendsInitializer initializer;

    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "WQDR00001-2025-Q3";

        Request request = Request
                .builder()
                .id(requestId)
                .type(RequestType.WASTE_QDR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .payload(WasteQDRRequestPayload
                        .builder()
                        .qdr(WasteQDR.builder().build().builder().build())
                        .build()
                )
                .metadata(WasteQDRRequestMetaData.builder().year(Year.of(2025)).build())
                .build();

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.WASTE_QDR_WAIT_FOR_AMENDS_PAYLOAD)
                .qdr(((WasteQDRRequestPayload) request.getPayload()).getQdr())
                .build();

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.WASTE_QDR_WAIT_FOR_AMENDS);
    }
}
