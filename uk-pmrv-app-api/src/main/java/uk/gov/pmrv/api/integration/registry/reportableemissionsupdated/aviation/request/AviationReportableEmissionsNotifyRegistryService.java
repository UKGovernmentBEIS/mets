package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.common.reporting.domain.ReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;

import java.math.RoundingMode;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.MISSING_REGISTRY_ID_ERROR_MESSAGE;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.capitalizeFirstLetter;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
class AviationReportableEmissionsNotifyRegistryService {

	private static final String SERVICE_KEY = "aviation";
	private static final String INTEGRATION_POINT_KEY = "reportable-emissions-updated";
	private static final String AVIATION_REPORTABLE_PERIOD_START_KEY = "aer.aviation.reporting-period.from";
	private static final String AVIATION_REPORTABLE_PERIOD_END_KEY = "aer.aviation.reporting-period.to";

	private final AviationAccountQueryService aviationAccountQueryService;
	private final AviationAerRequestQueryService aviationAerRequestQueryService;
	private final AviationReportableEmissionsSendToRegistryProducer reportableEmissionsSendToRegistryProducer;
	private final KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> aviationAccountEmissionsUpdatedKafkaTemplate;
	private final ConfigurationService configurationService;
	private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;
	private final AviationRegistryIntegrationEmailProperties emailProperties;
	private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

	@Transactional
	public void notifyRegistry(AviationReportableEmissionsUpdatedEvent event) {
		Long accountId = event.getAccountId();
		final AviationAccountInfoDTO account = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);
		if(account.getEmissionTradingScheme() != EmissionTradingScheme.UK_ETS_AVIATION){
			log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no uk-ets schema");
			return;
		}

        if (isExempt(event)) {
			return;
		}

		if (event.isFromDre()) {
			notifyRegistry(event, account);
			return;
		}

		Optional<Request> aerRequest = aviationAerRequestQueryService
			.findRequestByAccountAndTypeForYear(accountId, RequestType.AVIATION_AER_UKETS, event.getYear());

		if (aerRequest.isEmpty()) {

			log.error(REQUEST_LOG_FORMAT,
				SERVICE_KEY,
				event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Cannot send emissions to ETS Registry because no aer request has been found");

			throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_AVIATION_AER_NOT_FOUND, event);
		}

		AviationAerUkEtsRequestPayload aerRequestPayload =  ((AviationAerUkEtsRequestPayload) aerRequest.get().getPayload());

		if (aviationAerConditionsAreSatisfied(event, aerRequestPayload)) {
			notifyRegistry(event, account);
		}
	}

	private boolean aviationAerConditionsAreSatisfied(AviationReportableEmissionsUpdatedEvent event,
													  AviationAerUkEtsRequestPayload aerRequestPayload) {
		boolean isVerificationPerformed = aerRequestPayload.isVerificationPerformed();
		if (!isVerificationPerformed && !event.isFromRegulator()) {
			log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Emissions updated are not sent to ETS Registry on operator non-verified AER submission");
			return false;
		}
		if (isVerificationPerformed && event.isFromRegulator()) {
			log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Emissions updated are not sent to ETS Registry on regulator verified AER review completed");
			return false;
		}

		return reportingPeriodConditionsAreSatisfied(event);
	}

	private boolean isExempt(AviationReportableEmissionsUpdatedEvent event){
		AviationReportableEmissionsEntity aviationReportableEmissions =
				aviationReportableEmissionsRepository.findByAccountIdAndYear(event.getAccountId(), event.getYear())
						.orElse( null);

		if(aviationReportableEmissions == null || aviationReportableEmissions.isExempted()) {
			log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Not emission will be published for except account");
			return true;
		}
		log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Continue the process account was not exempt.");
		return false;
	}

	private boolean reportingPeriodConditionsAreSatisfied(ReportableEmissionsUpdatedEvent event) {
		if (!event.getYear().equals(Year.now().minusYears(1))) {
			log.info(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					String.format("Emissions updated are not sent to ETS Registry because AER year %d is not last year",
							event.getYear().getValue()));
			return false;
		}

		Optional<ConfigurationDTO> reportingPeriodFromConfiguration = configurationService
				.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_START_KEY);
		Optional<ConfigurationDTO> reportingPeriodToConfiguration = configurationService
				.getConfigurationByKey(AVIATION_REPORTABLE_PERIOD_END_KEY);


		if (reportingPeriodFromConfiguration.isEmpty()) {
			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.from property has been found");

			throw new BusinessException(
					MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_AVIATION_REPORTING_PERIOD_FROM_NOT_FOUND,
					event);
		}

		if (reportingPeriodToConfiguration.isEmpty()) {
			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.from property has been found");

			throw new BusinessException(
					MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_AVIATION_REPORTING_PERIOD_TO_NOT_FOUND,
					event);
		}

		MonthDay reportingPeriodFrom  =  MonthDay.parse(reportingPeriodFromConfiguration.get().getValue().toString(),
				DateTimeFormatter.ofPattern("dd/MM"));

		MonthDay reportingPeriodTo =  MonthDay.parse(reportingPeriodToConfiguration.get().getValue().toString(),
				DateTimeFormatter.ofPattern("dd/MM"));

		if ((MonthDay.now().isAfter(reportingPeriodFrom) || MonthDay.now().equals(reportingPeriodFrom)) &&
				(MonthDay.now().isBefore(reportingPeriodTo) || MonthDay.now().equals(reportingPeriodTo)) ) {
			return true;
		} else {
			log.info(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Emissions updated are not sent to ETS Registry because AER (initiator type: AER), is not submitted within the reporting period");
			return false;
		}
	}

	private void notifyRegistry(ReportableEmissionsUpdatedEvent event, AviationAccountInfoDTO account) {

		if (ObjectUtils.isEmpty(account.getRegistryId())) {
			log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because account doesn't have a registry id");

			notifyRegulator(event, account);
			return;
		}

		BigDecimal roundedEmissions = event.getReportableEmissions().setScale(0, RoundingMode.HALF_UP);
		String reportableEmissions;
		// In case reportableEmissions cannot fit in a long type (as per registry's type) log an error but send emissions anyway
		try {
			reportableEmissions = String.valueOf(roundedEmissions.longValueExact());
		} catch (ArithmeticException e) {
			reportableEmissions = roundedEmissions.toString();
			log.error(REQUEST_LOG_FORMAT,
				SERVICE_KEY,
				event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Emissions value overflow " + reportableEmissions);
		}

		final AccountEmissionsUpdatedRequestEvent accountEmissionsUpdatedRequestEvent = AccountEmissionsUpdatedRequestEvent.builder()
				.registryId(account.getRegistryId()).reportableEmissions(reportableEmissions)
				.reportingYear(event.getYear()).build();

		reportableEmissionsSendToRegistryProducer.produce(accountEmissionsUpdatedRequestEvent,
				aviationAccountEmissionsUpdatedKafkaTemplate);

		log.info(REQUEST_LOG_FORMAT, SERVICE_KEY, event.getAccountId(),
				INTEGRATION_POINT_KEY, "Emissions sent to registry " + accountEmissionsUpdatedRequestEvent);
	}

	private void notifyRegulator(ReportableEmissionsUpdatedEvent event, AviationAccountInfoDTO account) {

		final Map<String, Object> templateParams = new HashMap<>();
		templateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId());
		templateParams.put(PmrvEmailNotificationTemplateConstants.ERROR_MESSAGE, MISSING_REGISTRY_ID_ERROR_MESSAGE);
		templateParams.put(PmrvEmailNotificationTemplateConstants.YEAR, event.getYear().getValue());
		templateParams.put(PmrvEmailNotificationTemplateConstants.EMISSIONS, event.getReportableEmissions());
		templateParams.put(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, capitalizeFirstLetter(SERVICE_KEY));
		templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName());

		final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.competentAuthority(account.getCompetentAuthority())
						.templateName(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_MISSING_REGISTRY_ID.getName())
						.accountType(account.getAccountType())
						.templateParams(templateParams)
						.build())
				.build();
		notificationEmailService.notifyRecipient(emailData, emailProperties.getEmail().get(account.getCompetentAuthority().getCode()));
	}
}
