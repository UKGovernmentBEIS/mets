package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestQueryService;

@ExtendWith(MockitoExtension.class)
class InstallationReportableEmissionsNotifyRegistryServiceTest {

	@InjectMocks
	private InstallationReportableEmissionsNotifyRegistryService cut;

	@Mock
	private InstallationAccountQueryService installationAccountQueryService;
	
	@Mock
	private InstallationReportableEmissionsSendToRegistryProducer reportableEmissionsSendToRegistryProducer;
	
	@Mock
	private KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> installationAccountEmissionsUpdatedKafkaTemplate;

	@Mock
	private AerRequestQueryService aerRequestQueryService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private InstallationRegistryIntegrationEmailProperties emailProperties;

	@Mock
	private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;


	@Test
	void notifyRegistry_accountConditionsNotSatisfied_emitterTypeIsNotGHGE_doNotSend() {

    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.HSE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L,Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_accountConditionsNotSatisfied_emitterTypeIsNotSpecified_send() {
				InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(true, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);
		aerRequest.setPayload(null);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), "any@mail.c"));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
		verify(notificationEmailService, times(1)).notifyRecipient(
			any(),
			any()
		);

	}

	@Test
	void notifyRegistry_eventIsFromDRE_accountHasRegistryId_send(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(true, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent, installationAccountEmissionsUpdatedKafkaTemplate);
	}

	@Test
	void notifyRegistry_longValue_send(){
		InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(true, false, Year.now().minusYears(1));
		event.setReportableEmissions(new BigDecimal(1234567890123.34));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent, installationAccountEmissionsUpdatedKafkaTemplate);
	}

	@Test
	void notifyRegistry_eventIsFromDRE_accountDoesNotHaveRegistryId_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(true, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(false, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), "any@mail.c"));
		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
		verify(notificationEmailService, times(1)).notifyRecipient(
				any(),
				any()
		);
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerRequestDoesNotExist_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.empty());

		final BusinessException be = assertThrows(BusinessException.class,() -> {
			cut.notifyRegistry(event);
        });

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_AER_NOT_FOUND);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_verifiedAerComesFromRegulator_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, true, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_nonVerifiedAerComesFromOperator_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( false, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerInitiatorTypeNotAerNotRevocationNotSurrender_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.PERMIT_VARIATION, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));

		final BusinessException be = assertThrows(BusinessException.class,() -> {
			cut.notifyRegistry(event);
        });

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_WRONG_AER_INITIATOR_TYPE);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerInitiatorTypeAer_reportingPeriodFromConfigurationDoesNotExist_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.from")).thenReturn(Optional.empty());
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.to")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.to")
				.value("31/12")
				.build()));

		final BusinessException be = assertThrows(BusinessException.class,() -> {
			cut.notifyRegistry(event);
        });

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_FROM_NOT_FOUND);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerInitiatorTypeAer_reportingPeriodToConfigurationDoesNotExist_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.to")).thenReturn(Optional.empty());
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.from")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.from")
				.value("01/01")
				.build()));

		final BusinessException be = assertThrows(BusinessException.class,() -> {
			cut.notifyRegistry(event);
        });

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_FROM_NOT_FOUND);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerInitiatorTypeAer_submittedYearIsNotPreviousYear_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false,  Year.now().minusYears(2));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(2).getValue())).thenReturn(Optional.of(aerRequest));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(2).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
			AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
	}

	@Test
	void notifyRegistry_aerConditionsNotSatisfied_aerInitiatorTypeAer_outsideOfReportingPeriod_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.from")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.from")
				.value("01/01")
				.build()));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.to")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.to")
				.value("30/04")
				.build()));

		ZoneId zone = ZoneId.of("UTC");
        final Clock fixedClock = Clock.fixed(LocalDate.of(Year.now().getValue(), 7,5).atStartOfDay(zone).toInstant(), zone);
        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);

			cut.notifyRegistry(event);

			verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
			verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
			verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(
				AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
		}
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeAer_accountHasRegistryId_send(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.from")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.from")
				.value("01/01")
				.build()));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.to")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.to")
				.value("30/12")
				.build()));

		ZoneId zone = ZoneId.of("UTC");
        final Clock fixedClock = Clock.fixed(LocalDate.of(Year.now().getValue(), 7,5).atStartOfDay(zone).toInstant(), zone);
        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);

			cut.notifyRegistry(event);

			verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
			verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
			verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent, installationAccountEmissionsUpdatedKafkaTemplate);
		}
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeAer_accountDoesNotHaveRegistryId_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false,  Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(false, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.AER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.from")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.from")
				.value("01/01")
				.build()));
		when(configurationService.getConfigurationByKey("aer.installation.reporting-period.to")).thenReturn(Optional.of(ConfigurationDTO
				.builder()
				.key("aer.installation.reporting-period.to")
				.value("30/12")
				.build()));

		ZoneId zone = ZoneId.of("UTC");
        final Clock fixedClock = Clock.fixed(LocalDate.of(Year.now().getValue(), 7,5).atStartOfDay(zone).toInstant(), zone);
        try (MockedStatic<Clock> mockClock = Mockito.mockStatic(Clock.class)) {
            mockClock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
			when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), "any@mail.c"));

			cut.notifyRegistry(event);

			verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
			verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
			verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
			verify(notificationEmailService, times(1)).notifyRecipient(
					any(),
					any()
			);
		}
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeRevocation_accountHasRegistryId_send(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.PERMIT_REVOCATION, PermitType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));


		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent, installationAccountEmissionsUpdatedKafkaTemplate);
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeRevocation_accountDoesNotHaveRegistryId_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(false, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.PERMIT_REVOCATION, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), "any@mail.c"));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
		verify(notificationEmailService, times(1)).notifyRecipient(
				any(),
				any()
		);
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeSurrender_accountHasRegistryId_send(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(true, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest(true, RequestType.PERMIT_SURRENDER, PermitType.GHGE);
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = getEmissionsUpdatedEvent(event, account);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));


			cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent, installationAccountEmissionsUpdatedKafkaTemplate);
	}

	@Test
	void notifyRegistry_aerConditionsSatisfied_aerInitiatorTypeSurrender_accountDoesNotHaveRegistryId_doNotSend(){
    	InstallationReportableEmissionsUpdatedEvent event = getDummyEvent(false, false, Year.now().minusYears(1));
		InstallationAccountInfoDTO account = getDummyAccount(false, EmitterType.GHGE);
		Request aerRequest = getDummyAerRequest( true, RequestType.PERMIT_SURRENDER, PermitType.GHGE);

		when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L)).thenReturn(account);
		when(aerRequestQueryService.findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue())).thenReturn(Optional.of(aerRequest));
		when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), "any@mail.c"));

		cut.notifyRegistry(event);

		verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(1L);
		verify(aerRequestQueryService, times(1)).findAerByAccountIdAndYear(1L, Year.now().minusYears(1).getValue());
		verify(reportableEmissionsSendToRegistryProducer, never()).produce(Mockito.any(AccountEmissionsUpdatedRequestEvent.class), Mockito.any(KafkaTemplate.class));
		verify(notificationEmailService, times(1)).notifyRecipient(
				any(),
				any()
		);
	}

	private Request getDummyAerRequest(Boolean isVerificationPerfomed, RequestType initiatorRequestType, PermitType permitType) {
		AerRequestPayload requestPayload = AerRequestPayload
				.builder()
				.verificationPerformed(isVerificationPerfomed)
				.permitOriginatedData(PermitOriginatedData
						.builder()
						.permitType(permitType)
						.build())
				.build();

		AerRequestMetadata requestMetadata = AerRequestMetadata
				.builder()
				.initiatorRequest(AerInitiatorRequest
						.builder()
						.submissionDateTime(LocalDateTime.now())
						.type(initiatorRequestType)
						.build())
				.build();

		return Request
				.builder()
				.payload(requestPayload)
				.metadata(requestMetadata)
				.type(RequestType.AER)
				.accountId(1L)
				.status(RequestStatus.IN_PROGRESS)
				.build();
	}

	private AccountEmissionsUpdatedRequestEvent getEmissionsUpdatedEvent(InstallationReportableEmissionsUpdatedEvent event, InstallationAccountInfoDTO account) {
		return AccountEmissionsUpdatedRequestEvent.builder().registryId(account.getRegistryId())
				.reportableEmissions(event.getReportableEmissions().setScale(0, RoundingMode.HALF_UP).toString()).reportingYear(event.getYear()).build();
	}

	private InstallationReportableEmissionsUpdatedEvent getDummyEvent(Boolean isFromDRE, Boolean isFromRegulator, Year year) {
		Long accountId = 1L;
		return InstallationReportableEmissionsUpdatedEvent.builder()
    			.accountId(accountId)
    			.isFromDre(isFromDRE)
    			.reportableEmissions(new BigDecimal(10.34))
				.isFromRegulator(isFromRegulator)
    			.year(year)
    			.build();
	}

	private InstallationAccountInfoDTO getDummyAccount(Boolean hasRegistryId, EmitterType emitterType) {
		Long accountId = 1L;
		return InstallationAccountInfoDTO.builder()
				.id(accountId)
				.emitterType(emitterType)
				.registryId(hasRegistryId ? 1: null)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
	}

}
