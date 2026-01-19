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
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRQuarter;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class WasteQDRApplicationRegulatorReviewSubmitInitializerTest {

    @InjectMocks
    private WasteQDRApplicationRegulatorReviewSubmitInitializer initializer;

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
                        .qdr(WasteQDR
                                .builder()
                                .build())
                        .build()
                )
                .metadata(WasteQDRRequestMetaData.builder().year(Year.of(2025)).quarter(WasteQDRQuarter.Q3).build())
                .build();

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .qdr(((WasteQDRRequestPayload) request.getPayload()).getQdr())
                .build();


        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);
    }


    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
