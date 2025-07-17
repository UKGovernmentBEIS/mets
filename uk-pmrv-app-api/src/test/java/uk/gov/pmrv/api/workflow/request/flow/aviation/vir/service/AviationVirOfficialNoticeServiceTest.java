package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@ExtendWith(MockitoExtension.class)
class AviationVirOfficialNoticeServiceTest {

    @InjectMocks
    private AviationVirOfficialNoticeService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Test
    void generateAndSaveRecommendedImprovementsOfficialNotice() {
        final String requestId = "AEM-001";
        final String signatory = "Signatory";
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("op1"))
                .signatory(signatory)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(AviationVirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("primary@email").userId("op1").build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");
        final DocumentTemplateParamsSourceData paramsSourceData = DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.AVIATION_VIR_REVIEWED)
                .request(request)
                .signatory(signatory)
                .accountPrimaryContact(accountPrimaryContact)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails)
                .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("Recommended_improvements.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(ccRecipientsEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(paramsSourceData))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(DocumentTemplateType.AVIATION_VIR_REVIEWED,
                templateParams, "Recommended_improvements.pdf")).thenReturn(officialNotice);

        // Invoke
        service.generateAndSaveRecommendedImprovementsOfficialNotice(requestId);

        // Verify
        assertThat(request.getPayload()).isInstanceOf(AviationVirRequestPayload.class);
        assertThat(((AviationVirRequestPayload) request.getPayload()).getOfficialNotice()).isEqualTo(officialNotice);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
                .findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1))
                .constructTemplateParams(paramsSourceData);
        verify(documentFileGeneratorService, times(1))
                .generateAndSaveFileDocument(DocumentTemplateType.AVIATION_VIR_REVIEWED, templateParams, "Recommended_improvements.pdf");
    }

    @Test
    void sendOfficialNotice() {
        final String requestId = "AEM-001";
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("op1"))
                .signatory("Signatory")
                .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("recommended_improvements.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(AviationVirRequestPayload.builder()
                        .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .officialNotice(officialNotice)
                        .build())
                .build();

        final List<String> ccRecipientsEmails = List.of("operator1@email");

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(ccRecipientsEmails);

        // Invoke
        service.sendOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(decisionNotificationUsersService, times(1))
                .findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(officialNotice), request, ccRecipientsEmails);
    }
}
