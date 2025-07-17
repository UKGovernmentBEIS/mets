package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.LocalDateTime;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaCreateActionHandlerTest {

    @InjectMocks
    private AviationDoECorsiaCreateActionHandler handler;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        Long accountId = 1L;
        String aerRequestId = "aerRequestId";
        Year year = Year.of(2023);
        ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId(aerRequestId)
                .build();
        AppUser appUser = AppUser.builder().userId("user").build();

        LocalDateTime initiatorRequestSubmissionDate = LocalDateTime.now();
        AerInitiatorRequest initiatorRequest = AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_CORSIA).submissionDateTime(initiatorRequestSubmissionDate).build();
        RequestDetailsDTO aerRequestDetails = new RequestDetailsDTO(aerRequestId, RequestType.AVIATION_AER_CORSIA,
                RequestStatus.IN_PROGRESS, LocalDateTime.now(),
                AviationAerRequestMetadata.builder().year(year).initiatorRequest(initiatorRequest).build());

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AVIATION_DOE_CORSIA)
                .accountId(accountId)
                .requestMetadata(AviationDoECorsiaRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_DOE_CORSIA)
                        .year(((AviationAerRequestMetadata) aerRequestDetails.getRequestMetadata()).getYear())
                        .build())
                .requestPayload(AviationDoECorsiaRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_DOE_CORSIA_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .reportingYear(year)
                        .initiatorRequest(initiatorRequest)
                        .build())
                .build();

        when(requestQueryService.findRequestDetailsById(aerRequestId)).thenReturn(aerRequestDetails);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("DOECORSIA00001-2025-1").build());

        String result = handler.process(accountId, payload, appUser);

        assertThat(result).isEqualTo("DOECORSIA00001-2025-1");
        verify(requestQueryService, times(1)).findRequestDetailsById(aerRequestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.AVIATION_DOE_CORSIA);
    }
}
