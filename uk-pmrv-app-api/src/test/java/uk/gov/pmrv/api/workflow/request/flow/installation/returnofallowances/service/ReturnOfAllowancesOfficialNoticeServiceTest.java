package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeGeneratorService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesOfficialNoticeServiceTest {
    @Mock
    private RequestService requestService;
    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;
    @Mock
    private OfficialNoticeSendService officialNoticeSendService;
    @Mock
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @InjectMocks
    private ReturnOfAllowancesOfficialNoticeService service;

    @Test
    void generateReturnOfAllowancesOfficialNotice() {
        String requestId = "123";
        DecisionNotification decisionNotification = new DecisionNotification();
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO expectedNotice = new FileInfoDTO();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(any(Request.class),
            any(DocumentTemplateGenerationContextActionType.class),
            any(DocumentTemplateType.class), any(AccountType.class), any(DecisionNotification.class), anyString()))
            .thenReturn(expectedNotice);

        FileInfoDTO result = service.generateReturnOfAllowancesOfficialNotice(requestId);

        verify(officialNoticeGeneratorService).generate(request,
            DocumentTemplateGenerationContextActionType.RETURN_OF_ALLOWANCES,
            DocumentTemplateType.RETURN_OF_ALLOWANCES, AccountType.INSTALLATION, decisionNotification,
            "Return_allowances_notice.pdf");
        assertEquals(expectedNotice, result);
    }

    @Test
    void sendOfficialNotice() {
        Request request = new Request();
        FileInfoDTO officialNotice = new FileInfoDTO();
        DecisionNotification decisionNotification = new DecisionNotification();

        final List<String> recipients = List.of("email@email.com");
        
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(recipients);
        
        service.sendOfficialNotice(request, officialNotice, decisionNotification);

        verify(decisionNotificationUsersService).findUserEmails(decisionNotification);
        verify(officialNoticeSendService).sendOfficialNotice(List.of(officialNotice), request, recipients);
    }
}
