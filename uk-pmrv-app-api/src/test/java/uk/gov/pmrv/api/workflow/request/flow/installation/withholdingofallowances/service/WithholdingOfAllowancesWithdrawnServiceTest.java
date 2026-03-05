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
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawnServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private Request request;

    @Mock
    private WithholdingOfAllowancesRequestPayload requestPayload;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private WithholdingOfAllowancesOfficialNoticeService withholdingOfAllowancesOfficialNoticeService;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawnService withdrawnService;

    @Test
    void withdraw() {
        final String requestId = "requestId";
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("Signature")
            .build();
        RequestActionUserInfo requestActionUserInfo = RequestActionUserInfo.builder().build();
        Map<String, RequestActionUserInfo> userInfo = Map.of("user", requestActionUserInfo);

        FileInfoDTO officialNotice = FileInfoDTO.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(request.getId()).thenReturn(requestId);
        when(requestPayload.getWithdrawDecisionNotification()).thenReturn(decisionNotification);
        when(requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(userInfo);
        when(withholdingOfAllowancesOfficialNoticeService.generateWithholdingOfAllowancesWithdrawnOfficialNotice(requestId))
            .thenReturn(officialNotice);

        withdrawnService.withdraw(requestId);

        verify(requestService).addActionToRequest(
            request,
            WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN_PAYLOAD)
                .withholdingWithdrawal(requestPayload.getWithholdingWithdrawal())
                .decisionNotification(decisionNotification)
                .officialNotice(officialNotice)
                .usersInfo(userInfo)
                .build(),
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN,
            request.getPayload().getRegulatorAssignee()
        );
        verify(withholdingOfAllowancesOfficialNoticeService).sendOfficialNotice(
            request,
            officialNotice,
            decisionNotification);

        verify(request).setSubmissionDate(any());
    }
}
