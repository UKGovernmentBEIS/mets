package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

@ExtendWith(MockitoExtension.class)
class InstallationPreviewOfficialNoticeServiceTest {

    @InjectMocks
    private InstallationPreviewOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private PermitIdentifierGenerator generator;


    @Test
    void sendOfficialNotice_sameServiceContact() {

        final long accountId = 3L;
        Request request = Request.builder().accountId(accountId).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();
        List<String> emails = List.of("cc1@email", "cc2@email");
        final DocumentTemplateParamsSourceData sourceData = DocumentTemplateParamsSourceData.builder()
            .request(request)
            .signatory("signatory")
            .ccRecipientsEmails(emails)
            .accountPrimaryContact(accountPrimaryContact)
            .toRecipientEmail(accountPrimaryContact.getEmail())
            .build();
        final String permitId = "permitId";

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(emails);
        when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(permitId));
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(sourceData))
            .thenReturn(TemplateParams.builder().accountParams(InstallationAccountTemplateParams.builder()
                    .emitterType("emitterType")
                    .installationCategory("installationCategory")
                .build()).build());

        final TemplateParams templateParams = service.generateCommonParams(request, decisionNotification);

        Assertions.assertNull(((InstallationAccountTemplateParams)templateParams.getAccountParams()).getEmitterType());
        Assertions.assertNull(((InstallationAccountTemplateParams)templateParams.getAccountParams()).getInstallationCategory());

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(
            sourceData
        );
    }
}
