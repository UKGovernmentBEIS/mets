package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

@ExtendWith(MockitoExtension.class)
class AviationPreviewOfficialNoticeServiceTest {

    @InjectMocks
    private AviationPreviewOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;

    @Mock
    private EmpIssuanceRequestIdGenerator generator;


    @Test
    void generateCommonParamsWithoutAccountNameLocation() {

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
        final String empId = "empId";

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(emails);
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(sourceData))
            .thenReturn(TemplateParams.builder().accountParams(AviationAccountTemplateParams.builder()
                    .name("name")
                    .location("location")
                .build()).build());

        final TemplateParams templateParams = service.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);

        Assertions.assertNull(templateParams.getAccountParams().getName());
        Assertions.assertNull(templateParams.getAccountParams().getLocation());

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(
            sourceData
        );
    }

    @Test
    void generateCommonParams_primaryContact_required_and_exists() {
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
        final String empId = "empId";
        final RequestParams requestParams = RequestParams.builder().accountId(accountId).build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(emails);
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(sourceData))
                .thenReturn(TemplateParams.builder().accountParams(AviationAccountTemplateParams.builder()
                        .name("name")
                        .location("location")
                        .build()).build());
        when(generator.generate(requestParams)).thenReturn(empId);

        final TemplateParams templateParams = service.generateCommonParams(request, decisionNotification, true);

        assertEquals("name", templateParams.getAccountParams().getName());
        assertEquals("location", templateParams.getAccountParams().getLocation());

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(
                sourceData
        );
    }
    
    @Test
    void generateCommonParams_primaryContact_required_and_not_exists() {
		Request request = Request.builder().accountId(1L).build();
		final DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

		when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());

		BusinessException be = assertThrows(BusinessException.class,
				() -> service.generateCommonParams(request, decisionNotification, true));

		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND);

		verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
		verifyNoInteractions(decisionNotificationUsersService, documentTemplateOfficialNoticeParamsProvider);
    }
    
    @Test
    void generateCommonParams_primaryContact_not_required_and_not_exists() {
        final long accountId = 3L;
        Request request = Request.builder().accountId(accountId).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();
        Optional<UserInfoDTO> accountPrimaryContactOpt = Optional.empty();
        List<String> emails = List.of("cc1@email", "cc2@email");
        final DocumentTemplateParamsSourceData sourceData = DocumentTemplateParamsSourceData.builder()
                .request(request)
                .signatory("signatory")
                .ccRecipientsEmails(emails)
                .accountPrimaryContact(null)
                .toRecipientEmail(null)
                .build();
        final String empId = "empId";
        final RequestParams requestParams = RequestParams.builder().accountId(accountId).build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(accountPrimaryContactOpt);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(emails);
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(sourceData))
                .thenReturn(TemplateParams.builder().accountParams(AviationAccountTemplateParams.builder()
                        .name("name")
                        .location("location")
                        .build()).build());
        when(generator.generate(requestParams)).thenReturn(empId);

        final TemplateParams templateParams = service.generateCommonParams(request, decisionNotification, false);

        assertEquals("name", templateParams.getAccountParams().getName());
        assertEquals("location", templateParams.getAccountParams().getLocation());

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(
                sourceData
        );
    }
}
