package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesSubmittedServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private ReturnOfAllowancesOfficialNoticeService officialNoticeService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @InjectMocks
    private ReturnOfAllowancesSubmittedService service;

    @Test
    void submit() {
        String requestId = "123";
        Request request = mock(Request.class);
        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .returnOfAllowances(ReturnOfAllowances.builder()
                .build())
            .build();
        FileInfoDTO officialNotice = FileInfoDTO.builder().build();
        RequestActionUserInfo requestActionUserInfo = RequestActionUserInfo.builder().build();
        Map<String, RequestActionUserInfo> userInfo = Map.of("user", requestActionUserInfo);

        ReturnOfAllowancesApplicationSubmittedRequestActionPayload expectedPayload =
            ReturnOfAllowancesApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD)
                .returnOfAllowances(requestPayload.getReturnOfAllowances())
                .decisionNotification(decisionNotification)
                .usersInfo(userInfo)
                .officialNotice(officialNotice)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(officialNoticeService.generateReturnOfAllowancesOfficialNotice(requestId)).thenReturn(officialNotice);
        when(requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(userInfo);

        service.submit(requestId);

        verify(requestService).addActionToRequest(eq(request), eq(expectedPayload),
            eq(RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED), any());
        verify(officialNoticeService).sendOfficialNotice(eq(request), eq(officialNotice), eq(decisionNotification));
    }
}
