package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingCreateActionHandlerTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private RequestService requestService;


    @Test
    void process() {
        final String requestId = "AEM-3YPO-00175-5";
        final Long accountId = 1L;
        final Year year = Year.of(2023);
        final String aerRequestId = "AEM00175-2023";
        final AviationAerCorsia3YearPeriodCreateActionPayload createActionPayload = AviationAerCorsia3YearPeriodCreateActionPayload
                .builder()
                .payloadType(RequestCreateActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CREATE_ACTION_PAYLOAD)
                .requestId(aerRequestId)
                .build();

        final AppUser appUser = AppUser.builder().userId("regulator").build();

        final Request aerRequest = Request.builder()
                                .id(aerRequestId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year).build())
                        .build();

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING)
                .accountId(accountId)
                .requestMetadata(AviationAerCorsia3YearPeriodOffsettingRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING)
                        .year(year)
                        .currentAerId(aerRequest.getId())
                        .years(List.of(year.minusYears(2),year.minusYears(1),year))
                        .build())
                .requestPayload(AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .build();

        when(requestService.findRequestById(aerRequestId)).thenReturn(aerRequest);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id(requestId).build());

        String result = handler.process(accountId, createActionPayload, appUser);

        assertThat(result).isEqualTo(requestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(requestService, times(1)).findRequestById(aerRequestId);
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING);
    }
}
