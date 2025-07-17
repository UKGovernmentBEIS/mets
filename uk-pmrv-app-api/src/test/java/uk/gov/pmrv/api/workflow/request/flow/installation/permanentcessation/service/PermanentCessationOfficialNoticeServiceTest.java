package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class PermanentCessationOfficialNoticeServiceTest {

    @InjectMocks
    private PermanentCessationOfficialNoticeService service;

    @Mock
    private RequestService requestService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Mock
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    private static final String REQUEST_ID = "REQ-123";
    private static final String FILENAME = "Permanent_cessation_notice.pdf";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generatePermanentCessationOfficialNotice_returnsExpectedFileInfo() {
        // Arrange
        Request request = new Request();
        PermanentCessationRequestPayload payload = new PermanentCessationRequestPayload();
        DecisionNotification decisionNotification = new DecisionNotification();
        payload.setDecisionNotification(decisionNotification);
        request.setPayload(payload);

        FileInfoDTO expectedFile = FileInfoDTO.builder().name(FILENAME).build();

        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(
                request,
                DocumentTemplateGenerationContextActionType.PERMANENT_CESSATION_SUBMITTED,
                DocumentTemplateType.PERMANENT_CESSATION,
                AccountType.INSTALLATION,
                decisionNotification,
                FILENAME))
                .thenReturn(expectedFile);

        // Act
        FileInfoDTO result = service.generatePermanentCessationOfficialNotice(REQUEST_ID);

        // Assert
        assertEquals(expectedFile, result);
        verify(requestService).findRequestById(REQUEST_ID);
        verify(officialNoticeGeneratorService).generate(
                request,
                DocumentTemplateGenerationContextActionType.PERMANENT_CESSATION_SUBMITTED,
                DocumentTemplateType.PERMANENT_CESSATION,
                AccountType.INSTALLATION,
                decisionNotification,
                FILENAME
        );
    }

    @Test
    void sendOfficialNotice_sendsToExpectedEmails() {
        // Arrange
        Request request = new Request();
        DecisionNotification decisionNotification = new DecisionNotification();
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder().name(FILENAME).build();
        List<String> userEmails = List.of("user1@example.com", "user2@example.com");

        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(userEmails);

        // Act
        service.sendOfficialNotice(request, fileInfoDTO, decisionNotification);

        // Assert
        verify(decisionNotificationUsersService).findUserEmails(decisionNotification);
        verify(officialNoticeSendService).sendOfficialNotice(List.of(fileInfoDTO), request, userEmails);
    }
}
