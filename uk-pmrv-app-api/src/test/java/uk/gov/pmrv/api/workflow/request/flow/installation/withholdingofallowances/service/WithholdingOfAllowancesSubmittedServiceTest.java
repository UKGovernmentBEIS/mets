package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesSubmittedServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private WithholdingOfAllowancesOfficialNoticeService withholdingOfAllowancesOfficialNoticeService;

    @InjectMocks
    private WithholdingOfAllowancesSubmittedService service;

    @Test
    void submit() {
        String requestId = "123";
        Request request = mock(Request.class);
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("Signature")
            .build();
        RequestActionUserInfo requestActionUserInfo = RequestActionUserInfo.builder().build();
        Map<String, RequestActionUserInfo> userInfo = Map.of("user", requestActionUserInfo);
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .withholdingOfAllowances(WithholdingOfAllowances.builder()
                .build())
            .build();

        FileInfoDTO officialNotice = FileInfoDTO.builder().build();

        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload expectedPayload =
            WithholdingOfAllowancesApplicationSubmittedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD)
            .withholdingOfAllowances(requestPayload.getWithholdingOfAllowances())
            .decisionNotification(decisionNotification)
            .usersInfo(userInfo)
            .officialNotice(officialNotice)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getId()).thenReturn(requestId);
        when(request.getPayload()).thenReturn(requestPayload);
        when(requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(userInfo);
        when(withholdingOfAllowancesOfficialNoticeService.generateWithholdingOfAllowancesOfficialNotice(requestId))
            .thenReturn(officialNotice);

        service.submit(requestId);

        verify(requestService).addActionToRequest(eq(request), eq(expectedPayload),
            eq(RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED), any());
        verify(withholdingOfAllowancesOfficialNoticeService).sendOfficialNotice(request, officialNotice,
            decisionNotification);
    }
}
