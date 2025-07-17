package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;

@ExtendWith(MockitoExtension.class)
class AviationReportableEmissionsNotifyRegistryServiceTest {

	private static final String AVIATION_REPORTABLE_PERIOD_START_KEY = "aer.aviation.reporting-period.from";
	private static final String AVIATION_REPORTABLE_PERIOD_END_KEY = "aer.aviation.reporting-period.to";

	@InjectMocks
	private AviationReportableEmissionsNotifyRegistryService cut;

	@Mock
	private AviationAccountQueryService aviationAccountQueryService;
	
	@Mock
	private AviationReportableEmissionsSendToRegistryProducer reportableEmissionsSendToRegistryProducer;
	
	@Mock
	private KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> aviationAccountEmissionsUpdatedKafkaTemplate;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

	@Mock
	private AviationAerRequestQueryService aviationAerRequestQueryService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

	@Mock
	private AviationRegistryIntegrationEmailProperties emailProperties;


	@Test
    void notifyRegistry_dre() {
    	Long accountId = 1L;
		Integer registryId = 1234567;
		Integer year = 2000;
		Double totalEmissions = 10.34;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, year, totalEmissions, true,true);
		AviationAccountInfoDTO account = createAccount(accountId, registryId);
		
		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = AccountEmissionsUpdatedRequestEvent.builder().registryId(account.getRegistryId())
				.reportableEmissions(event.getReportableEmissions().setScale(0, RoundingMode.HALF_UP).toString()).reportingYear(event.getYear()).build();
		
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(account);
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, year));

		cut.notifyRegistry(event);
		
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent,
				aviationAccountEmissionsUpdatedKafkaTemplate);
    }

	@Test
	void notifyRegistry_longValue_dre() {
		Long accountId = 1L;
		Integer registryId = 1234567;
		Integer year = 2000;
		Double totalEmissions = 1234567890123.34;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, year, totalEmissions, true,true);
		AviationAccountInfoDTO account = createAccount(accountId, registryId);

		AccountEmissionsUpdatedRequestEvent
			accountEmissionsUpdatedRequestEvent = AccountEmissionsUpdatedRequestEvent.builder().registryId(account.getRegistryId())
			.reportableEmissions(event.getReportableEmissions().setScale(0, RoundingMode.HALF_UP).toString()).reportingYear(event.getYear()).build();

		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(account);
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
			.thenReturn(reportableEmissions(accountId, year));

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, times(1)).produce(accountEmissionsUpdatedRequestEvent,
			aviationAccountEmissionsUpdatedKafkaTemplate);
	}
	
	@Test
    void notifyRegistry_not_dre_happyPath() {
		Year validYear = Year.now().minusYears(1);
    	Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 1234567;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_START_KEY).value("01/01").build())
		);
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_END_KEY).value("31/12").build())
		);

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(reportableEmissionsSendToRegistryProducer, times(1))
				.produce(producedAccountEvent(registryId, new BigDecimal(totalEmissions),validYear.getValue()),
				aviationAccountEmissionsUpdatedKafkaTemplate);
    }

	@Test
    void not_notifyRegistry_emissionTradingSchemeNot_UK_ETS_AVIATION() {

		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;
		AviationAccountInfoDTO account = createAccount(accountId, registryId);
		account.setEmissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS);
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(account);

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, never())
				.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
    }

	@Test
    void notifyRegistry_missing_aviationReportableEmissionsForSpecificYear() {

		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;
		AviationAccountInfoDTO account = createAccount(accountId, registryId);
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(account);

		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(Optional.empty());

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, never())
				.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
    }

	@Test
    void notifyRegistry_missing_aviationReportableEmissionsHasExempt() {

		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;
		AviationAccountInfoDTO account = createAccount(accountId, registryId);
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(account);
		Optional<AviationReportableEmissionsEntity> aviationReportableEmissionsEntity = reportableEmissions(accountId, validYear.getValue());
		aviationReportableEmissionsEntity.get().setExempted(true);
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(aviationReportableEmissionsEntity);

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, never())
				.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
    }

	@Test
	void not_notifyRegistry_no_registry_id_exist() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		event.setFromDre(true);
		Integer registryId = null;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(emailProperties.getEmail()).thenReturn(Map.of(ENGLAND.getCode(),"mail@m.co"));

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(notificationEmailService, times(1)).notifyRecipient(
				any(),
				any()
		);
	}

	@Test
	void notifyRegistry_isFromDre_registryId_exist_andYearIsEarlier() {
		Year validYear = Year.now().minusYears(2);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		event.setFromDre(true);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(reportableEmissionsSendToRegistryProducer, times(1))
				.produce(producedAccountEvent(registryId, new BigDecimal(totalEmissions),validYear.getValue()),
						aviationAccountEmissionsUpdatedKafkaTemplate);
	}

	@Test
	void not_notify_if_missing_startDate() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));

		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY)).thenReturn(Optional.empty());
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_END_KEY).value("31/12").build())
		);

		Assert.assertThrows(BusinessException.class, () -> cut.notifyRegistry(event));
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
	}


	@Test
	void not_notify_if_missing_endDate() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY)).thenReturn(Optional.empty());
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_END_KEY).value("01/01").build())
		);

		Assert.assertThrows(BusinessException.class, () -> cut.notifyRegistry(event));
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
	}

	@Test
	void not_notify_if_missing_startDate_wrongFormat() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_START_KEY).value("31-01").build())
		);
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_END_KEY).value("31/12").build())
		);

		Assert.assertThrows(DateTimeParseException.class, () -> cut.notifyRegistry(event));
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
	}

	@Test
	void not_notify_if_missing_endDate_wrongFormat() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, validYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_START_KEY).value("31/01").build())
		);
		when(configurationService.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY)).thenReturn(
				Optional.ofNullable(ConfigurationDTO.builder().key(AVIATION_REPORTABLE_PERIOD_END_KEY).value("31-12").build())
		);

		Assert.assertThrows(DateTimeParseException.class, () -> cut.notifyRegistry(event));
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
	}

	@Test
	void not_notify_if_beforeValidYear() {
		Year inValidYear = Year.now().minusYears(2);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, inValidYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, inValidYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, inValidYear)).thenReturn(Optional
			.of(getDummyAerRequest(false)));

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, inValidYear);
		verify(reportableEmissionsSendToRegistryProducer, never())
				.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
	}

	@Test
	void not_notify_if_outsideValidPeriod() {
		Year inValidYear = Year.now().minusYears(0);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, inValidYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
				.thenReturn(reportableEmissions(accountId, inValidYear.getValue()));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, inValidYear)).thenReturn(Optional
			.of(getDummyAerRequest(false)));

		cut.notifyRegistry(event);

		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, inValidYear);
		verify(reportableEmissionsSendToRegistryProducer, never())
				.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
	}

	@Test
	void not_notify_if_missing_aviationAerPayload() {

		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;

		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
			.thenReturn(reportableEmissions(accountId, 2000));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(
			Optional.empty());

		Assert.assertThrows(BusinessException.class, () -> cut.notifyRegistry(event));
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
	}

	@Test
	void not_notify_if_notVerified_operatorSubmit() {

		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, false);
		Integer registryId = 123456;

		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
			.thenReturn(reportableEmissions(accountId, 2000));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(false)));

		cut.notifyRegistry(event);
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
		verify(reportableEmissionsSendToRegistryProducer, never())
			.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
	}

	@Test
	void not_notify_if_verified_regulatorSubmit() {
		Year validYear = Year.now().minusYears(1);
		Long accountId = 1L;
		Double totalEmissions = 10.23;
		AviationReportableEmissionsUpdatedEvent event = createEvent(accountId, validYear.getValue(), totalEmissions, false, true);
		Integer registryId = 123456;

		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(createAccount(accountId, registryId));
		when(aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear()))
			.thenReturn(reportableEmissions(accountId, 2000));
		when(aviationAerRequestQueryService.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear)).thenReturn(Optional
			.of(getDummyAerRequest(true)));

		cut.notifyRegistry(event);
		verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
		verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(event.getAccountId(), event.getYear());
		verify(aviationAerRequestQueryService, times(1)).findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, validYear);
		verify(reportableEmissionsSendToRegistryProducer, never())
			.produce(any(AccountEmissionsUpdatedRequestEvent.class), any(KafkaTemplate.class));
	}

	private Request getDummyAerRequest(Boolean isVerificationPerfomed) {
		AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload
			.builder()
			.verificationPerformed(isVerificationPerfomed)
			.build();

		return Request
			.builder()
			.payload(requestPayload)
			.type(RequestType.AVIATION_AER_UKETS)
			.accountId(1L)
			.status(RequestStatus.IN_PROGRESS)
			.build();
	}

	private Optional<AviationReportableEmissionsEntity> reportableEmissions(Long accountId, Integer year) {
		AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
				.accountId(accountId)
				.year(Year.of(year))
				.reportableEmissions(BigDecimal.TEN)
				.build();
		return Optional.of(reportableEmissionsEntity);
	}
	
	private AviationAccountInfoDTO createAccount(Long accountId, Integer registryId) {
		return AviationAccountInfoDTO.builder()
				.id(accountId)
				.registryId(registryId)
				.emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
				.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
				.competentAuthority(ENGLAND)
				.build();
	}

	private AccountEmissionsUpdatedRequestEvent producedAccountEvent(Integer registryId, BigDecimal reportableEmissions, Integer year){
		return AccountEmissionsUpdatedRequestEvent.builder().registryId(registryId)
				.reportableEmissions(reportableEmissions.setScale(0, RoundingMode.HALF_UP).toString()).reportingYear(Year.of(year)).build();
	}

	private AviationReportableEmissionsUpdatedEvent createEvent(Long accountId, Integer year, Double totalEmissions, boolean isFromDre, boolean isFromRegulator) {
        return AviationReportableEmissionsUpdatedEvent.builder()
				.accountId(accountId)
				.isFromDre(isFromDre)
				.isFromRegulator(isFromRegulator)
				.reportableEmissions(new BigDecimal(totalEmissions))
				.year(Year.of(year))
				.build();
	}
}
