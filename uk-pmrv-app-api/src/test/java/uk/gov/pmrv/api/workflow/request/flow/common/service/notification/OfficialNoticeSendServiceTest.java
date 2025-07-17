package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthorityDTOByRequestResolverDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficialNoticeSendServiceTest {

	@InjectMocks
    private OfficialNoticeSendService service;
	
	@Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;
	
	@Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    
    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;

	@Mock
	private AccountQueryService accountQueryService;

	
	@Test
    void sendOfficialNotice_sameServiceContact() {
		Long accountId = 1L;
    	FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    	
    	Request request = Request.builder()
        		.id("1")
				.accountId(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.type(RequestType.PERMIT_ISSUANCE)
                .build();
    	
    	UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
                .build();
    	
    	FileDTO officialDocFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();

		CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
			.id(CompetentAuthorityEnum.ENGLAND)
			.name("competentAuthority")
			.email("competent@authority.com")
			.build();
    	
    	when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
    		.thenReturn(Optional.of(accountPrimaryContact));
		when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
			.thenReturn(Optional.of(accountPrimaryContact));
    	when(fileDocumentService.getFileDTO(officialDocFileInfoDTO.getUuid()))
    		.thenReturn(officialDocFileDTO);
		when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.INSTALLATION))
			.thenReturn(competentAuthority);

		when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);

		service.sendOfficialNotice(List.of(officialDocFileInfoDTO), request);
    	
    	verify(requestAccountContactQueryService, times(2)).getRequestAccountPrimaryContact(request);
		verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
		verify(fileDocumentService, times(1)).getFileDTO(officialDocFileInfoDTO.getUuid());

		verify(accountQueryService,times(1)).getAccountType(accountId);
		verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.INSTALLATION);

		ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
				Mockito.eq(List.of(accountPrimaryContact.getEmail())), Mockito.eq(Collections.emptyList()), Mockito.eq(Collections.emptyList()));
		EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
		assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
						.competentAuthority(CompetentAuthorityEnum.ENGLAND)
						.accountType(AccountType.INSTALLATION)
					.templateParams(Map.of(
							PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
							PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
							PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
					))
						.build())
				.attachments(Map.of(officialDocFileInfoDTO.getName(), officialDocFileDTO.getFileContent())).build());
	}
    
    @Test
    void sendOfficialNotice_with_cc_and_sameServiceContact() {
		Long accountId = 1L;
    	FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    	
    	Request request = Request.builder()
        		.id("1")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.accountId(accountId)
				.type(RequestType.PERMIT_ISSUANCE)
                .build();
    	
    	UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
                .build();
    	
    	FileDTO officialDocFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();

    	List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");

		CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
			.id(CompetentAuthorityEnum.ENGLAND)
			.name("competentAuthority")
			.email("competent@authority.com")
			.build();
    	
    	when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
    		.thenReturn(Optional.of(accountPrimaryContact));
		when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
			.thenReturn(Optional.of(accountPrimaryContact));
    	when(fileDocumentService.getFileDTO(officialDocFileInfoDTO.getUuid()))
    		.thenReturn(officialDocFileDTO);
		when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.INSTALLATION))
			.thenReturn(competentAuthority);
		when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);


		service.sendOfficialNotice(List.of(officialDocFileInfoDTO), request, ccRecipientsEmails);
    	
    	verify(requestAccountContactQueryService, times(2)).getRequestAccountPrimaryContact(request);
		verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
		verify(fileDocumentService, times(1)).getFileDTO(officialDocFileInfoDTO.getUuid());
		verify(accountQueryService,times(1)).getAccountType(accountId);
		verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.INSTALLATION);

		ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
				Mockito.eq(List.of(accountPrimaryContact.getEmail())), Mockito.eq(ccRecipientsEmails), Mockito.eq(Collections.emptyList()));
		EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
		assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
						.competentAuthority(CompetentAuthorityEnum.ENGLAND)
						.accountType(AccountType.INSTALLATION)
					.templateParams(Map.of(
							PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
							PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
							PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
					))
						.build())
				.attachments(Map.of(officialDocFileInfoDTO.getName(), officialDocFileDTO.getFileContent())).build());
	}

	@Test
	void sendOfficialNotice_with_cc_and_differentServiceContact() {

		Long accountId = 1L;

		FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
			.name("offDoc.pdf")
			.uuid(UUID.randomUUID().toString())
			.build();

		Request request = Request.builder()
			.id("1")
			.accountId(accountId)
			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
			.type(RequestType.PERMIT_ISSUANCE)
			.build();

		UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
			.firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
			.build();

		UserInfoDTO accountServiceContact = UserInfoDTO.builder()
			.firstName("fn").lastName("ln").email("service@email").userId("serviceUserId")
			.build();

		FileDTO officialDocFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();

		List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");

		CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
			.id(CompetentAuthorityEnum.ENGLAND)
			.name("competentAuthority")
			.email("competent@authority.com")
			.build();

		when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
			.thenReturn(Optional.of(accountPrimaryContact));
		when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
			.thenReturn(Optional.of(accountServiceContact));
		when(fileDocumentService.getFileDTO(officialDocFileInfoDTO.getUuid()))
			.thenReturn(officialDocFileDTO);
		when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.INSTALLATION))
			.thenReturn(competentAuthority);
		when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);


		service.sendOfficialNotice(List.of(officialDocFileInfoDTO), request, ccRecipientsEmails);

		verify(requestAccountContactQueryService, times(2)).getRequestAccountPrimaryContact(request);
		verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
		verify(fileDocumentService, times(1)).getFileDTO(officialDocFileInfoDTO.getUuid());

		verify(accountQueryService,times(1)).getAccountType(accountId);
		verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.INSTALLATION);



		ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
		ArgumentCaptor<List<String>> toRecipientsEmailsCaptor = ArgumentCaptor.forClass(List.class);
		verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
				toRecipientsEmailsCaptor.capture(),
				Mockito.eq(ccRecipientsEmails), Mockito.eq(Collections.emptyList()));
		List<String> toRecipientsEmailsCaptured = toRecipientsEmailsCaptor.getValue();
		assertThat(toRecipientsEmailsCaptured).containsExactlyInAnyOrder("primary@email", "service@email");
		
		EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
		assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
			.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
				.templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.accountType(AccountType.INSTALLATION)
				.templateParams(Map.of(
						PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
						PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
						PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
				))
				.build())
			.attachments(Map.of(officialDocFileInfoDTO.getName(), officialDocFileDTO.getFileContent())).build());
	}

	@Test
	void sendOfficialNotice_with_cc_and_bcc_whenDuplicatesInCc_thenRemoveDupe() {

		Long accountId = 1L;

		FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
			.name("offDoc.pdf")
			.uuid(UUID.randomUUID().toString())
			.build();

		Request request = Request.builder()
			.id("1")
			.accountId(accountId)
			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
			.type(RequestType.PERMIT_ISSUANCE)
			.build();

		UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
			.firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
			.build();

		FileDTO officialDocFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();

		List<String> ccRecipientsEmails = List.of("primary@email", "cc@email");
		List<String> bccRecipientsEmails = List.of("bcc@email");

		CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
			.id(CompetentAuthorityEnum.ENGLAND)
			.name("competentAuthority")
			.email("competent@authority.com")
			.build();

		when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
			.thenReturn(Optional.of(accountPrimaryContact));
		when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
			.thenReturn(Optional.of(accountPrimaryContact));
		when(fileDocumentService.getFileDTO(officialDocFileInfoDTO.getUuid()))
			.thenReturn(officialDocFileDTO);
		when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.INSTALLATION))
			.thenReturn(competentAuthority);
		when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);

		service.sendOfficialNotice(List.of(officialDocFileInfoDTO), request, ccRecipientsEmails, bccRecipientsEmails);

		verify(requestAccountContactQueryService, times(2)).getRequestAccountPrimaryContact(request);
		verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
		verify(fileDocumentService, times(1)).getFileDTO(officialDocFileInfoDTO.getUuid());
		verify(accountQueryService,times(1)).getAccountType(accountId);
		verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.INSTALLATION);


		ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
			Mockito.eq(List.of("primary@email")), Mockito.eq(List.of("cc@email")), Mockito.eq(bccRecipientsEmails));
		EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
		assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
			.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
				.templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.accountType(AccountType.INSTALLATION)
				.templateParams(Map.of(
						PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
						PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
						PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
				))
				.build())
			.attachments(Map.of(officialDocFileInfoDTO.getName(), officialDocFileDTO.getFileContent())).build());
	}

	@Test
	void getOfficialNoticeToRecipients() {
		Request request = Request.builder()
				.id("1")
				.build();

		UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
				.firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
				.build();

		UserInfoDTO accountServiceContact = UserInfoDTO.builder()
				.firstName("fn").lastName("ln").email("service@email").userId("serviceUserId")
				.build();

		when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
				.thenReturn(Optional.of(accountPrimaryContact));
		when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
				.thenReturn(Optional.of(accountServiceContact));

		Set<UserInfoDTO> defaultOfficialNoticeRecipients = service.getOfficialNoticeToRecipients(request);

		assertEquals(Set.of(accountPrimaryContact, accountServiceContact), defaultOfficialNoticeRecipients);
	}
}
