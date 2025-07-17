package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationRegulatorReviewSubmitInitializerTest {


    @InjectMocks
    private BDRApplicationRegulatorReviewSubmitInitializer initializer;

    @Mock
    private RequestVerificationService requestVerificationService;


    @Test
    void initializePayload() {

        Long accountId = 1L;
        String requestId = "BDR00001-2025";

        Request request = Request
                 .builder()
                 .id(requestId)
                 .type(RequestType.BDR)
                 .accountId(accountId)
                 .status(RequestStatus.IN_PROGRESS)
                 .payload(BDRRequestPayload
                         .builder()
                         .bdr(BDR
                                 .builder()
                                 .build())
                         .build()
                 )
                 .metadata(BDRRequestMetadata.builder().year(Year.of(2025)).build())
                 .build();

        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .bdr( ((BDRRequestPayload) request.getPayload()).getBdr())
                .bdrAttachments( ((BDRRequestPayload) request.getPayload()).getBdrAttachments())
                .bdrSectionsCompleted( ((BDRRequestPayload) request.getPayload()).getBdrSectionsCompleted())
                .build();


        BDRApplicationRegulatorReviewSubmitRequestTaskPayload actualTaskPayload =
                (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(actualTaskPayload).isEqualTo(taskPayload);


        verify(requestVerificationService, times(1))
                .refreshVerificationReportVBDetails(any(), eq(request.getVerificationBodyId()));
    }

    @Test
    void getRequestTaskTypes(){
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();
        assertThat(requestTaskTypes).containsExactlyInAnyOrder(RequestTaskType.BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
