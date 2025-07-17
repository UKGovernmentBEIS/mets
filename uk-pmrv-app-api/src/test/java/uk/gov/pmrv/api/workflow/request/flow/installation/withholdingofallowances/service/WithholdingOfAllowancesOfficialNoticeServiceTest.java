package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeGeneratorService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesOfficialNoticeServiceTest {
    @Mock
    private RequestService requestService;
    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;
    @Mock
    private OfficialNoticeSendService officialNoticeSendService;
    @Mock
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @InjectMocks
    private WithholdingOfAllowancesOfficialNoticeService service;

    @Test
    void generateWithholdingOfAllowancesOfficialNotice() {
        String requestId = "123";
        DecisionNotification decisionNotification = new DecisionNotification();
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO expectedNotice = new FileInfoDTO();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(any(Request.class),
            any(DocumentTemplateGenerationContextActionType.class),
            any(DocumentTemplateType.class), any(AccountType.class), any(DecisionNotification.class), anyString()))
            .thenReturn(expectedNotice);

        FileInfoDTO result = service.generateWithholdingOfAllowancesOfficialNotice(requestId);

        verify(officialNoticeGeneratorService).generate(request,
            DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES,
            DocumentTemplateType.WITHHOLDING_OF_ALLOWANCES, AccountType.INSTALLATION, decisionNotification,
            "Withholding_of_allowances_notice.pdf");
        assertEquals(expectedNotice, result);
    }

    @Test
    void generateWithholdingOfAllowancesWithdrawnOfficialNotice() {
        String requestId = "123";
        DecisionNotification decisionNotification = new DecisionNotification();
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder()
            .withdrawDecisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO expectedNotice = new FileInfoDTO();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(any(Request.class),
            any(DocumentTemplateGenerationContextActionType.class),
            any(DocumentTemplateType.class), any(AccountType.class), any(DecisionNotification.class), anyString()))
            .thenReturn(expectedNotice);

        FileInfoDTO result = service.generateWithholdingOfAllowancesWithdrawnOfficialNotice(requestId);

        verify(officialNoticeGeneratorService).generate(request,
            DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWN,
            DocumentTemplateType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWN, AccountType.INSTALLATION, decisionNotification,
            "Withdrawal_of_withholding_of_allowances_notice.pdf");
        assertEquals(expectedNotice, result);
    }

    @Test
    void sendOfficialNotice() {
        Request request = new Request();
        FileInfoDTO officialNotice = new FileInfoDTO();
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(List.of("email@email"));
        
        service.sendOfficialNotice(request, officialNotice, decisionNotification);
        
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialNotice), request, List.of("email@email"));
    }
}
