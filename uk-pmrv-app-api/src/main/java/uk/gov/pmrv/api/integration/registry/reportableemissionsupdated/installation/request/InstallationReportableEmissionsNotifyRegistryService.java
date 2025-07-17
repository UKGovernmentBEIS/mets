package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.common.reporting.domain.ReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestQueryService;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.MISSING_REGISTRY_ID_ERROR_MESSAGE;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.capitalizeFirstLetter;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
class InstallationReportableEmissionsNotifyRegistryService {

	private static final String SERVICE_KEY = "installation";
	private static final String INTEGRATION_POINT_KEY = "reportable-emissions-updated";
	private final InstallationAccountQueryService installationAccountQueryService;
	private final InstallationReportableEmissionsSendToRegistryProducer reportableEmissionsSendToRegistryProducer;
	private final KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> installationAccountEmissionsUpdatedKafkaTemplate;
	private final AerRequestQueryService aerRequestQueryService;
	private final ConfigurationService configurationService;
	private final InstallationRegistryIntegrationEmailProperties emailProperties;
	private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

	@Transactional
	public void notifyRegistry(InstallationReportableEmissionsUpdatedEvent event) {

		Long accountId = event.getAccountId();
		final InstallationAccountInfoDTO account = installationAccountQueryService.getInstallationAccountInfoDTOById(accountId);
		Optional<Request> aerRequest = aerRequestQueryService.findAerByAccountIdAndYear(account.getId(), event.getYear().getValue());

		if (aerRequest.isEmpty()) {

			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no aer request has been found");

			throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_AER_NOT_FOUND, event);
		}

		AerRequestPayload aerRequestPayload =  ((AerRequestPayload) aerRequest.get().getPayload());
		AerRequestMetadata aerRequestMetadata =  ((AerRequestMetadata) aerRequest.get().getMetadata());

		if (!isPermitTypeRetrievable(aerRequestPayload, account.getId())) {
			notifyRegulator(event, account, PmrvNotificationTemplateName.REGISTRY_INTEGRATION_MISSING_GHGE_HSE_FLAG);
			return;
		}

		if (!accountConditionsAreSatisfied(aerRequestPayload, account)) {
			return;
		}

		if (event.isFromDre()) {
			notifyRegistry(event, account);
			return;
		}

		if (aerConditionsAreSatisfied(aerRequestPayload, aerRequestMetadata, event)) {
			notifyRegistry(event, account);
		}
	}

	private boolean isPermitTypeRetrievable(AerRequestPayload aerRequestPayload, Long accountId) {
		// If aerRequestPayload is not specified (can happen in case of migrated data)
		if (Optional.ofNullable(aerRequestPayload).map(AerRequestPayload::getPermitOriginatedData).map(
			PermitOriginatedData::getPermitType).isEmpty()) {
			log.info(REQUEST_LOG_FORMAT,
				SERVICE_KEY,
				accountId,
				INTEGRATION_POINT_KEY,
				"Emissions updated are not sent to ETS Registry because permit type cannot be specified");
			return false;
		}
		return true;
	}

	private boolean accountConditionsAreSatisfied(AerRequestPayload aerRequestPayload, InstallationAccountInfoDTO account){
		if (!aerRequestPayload.getPermitOriginatedData().getPermitType().equals(PermitType.GHGE)) {
			log.info(REQUEST_LOG_FORMAT,
				SERVICE_KEY,
				account.getId(),
				INTEGRATION_POINT_KEY,
				"Emissions updated are not sent to ETS Registry because account is not of type GHGE");
			return false;
		}

		return true;
	}

	private boolean aerConditionsAreSatisfied(AerRequestPayload aerRequestPayload,
											  AerRequestMetadata aerRequestMetadata,
											  InstallationReportableEmissionsUpdatedEvent event){

		boolean isVerificationPerformed = aerRequestPayload.isVerificationPerformed();
		RequestType initiatorRequestType = aerRequestMetadata.getInitiatorRequest().getType();

		if (!isVerificationPerformed && !event.isFromRegulator()) {
			log.info(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Emissions updated are not sent to ETS Registry on operator non-verified AER submission");
			return false;
		}
		if (isVerificationPerformed && event.isFromRegulator()) {
			log.info(REQUEST_LOG_FORMAT,
				SERVICE_KEY,
				event.getAccountId(),
				INTEGRATION_POINT_KEY,
				"Emissions updated are not sent to ETS Registry on regulator verified AER review completed");
			return false;
		}


		if (initiatorRequestType.equals(RequestType.AER)) {
            return reportingPeriodConditionsAreSatisfied(event);
		} else if (initiatorRequestType.equals(RequestType.PERMIT_REVOCATION) || initiatorRequestType.equals(RequestType.PERMIT_SURRENDER)) {
			return true;
		} else {
			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Emissions updated are not sent to ETS Registry because AER initiator was not within the allowed types");
			throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_WRONG_AER_INITIATOR_TYPE, event, initiatorRequestType);
		}
	}

	private boolean reportingPeriodConditionsAreSatisfied(InstallationReportableEmissionsUpdatedEvent event) {

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
				.getConfigurationByKey("aer.installation.reporting-period.from");
		Optional<ConfigurationDTO> reportingPeriodToConfiguration = configurationService
				.getConfigurationByKey("aer.installation.reporting-period.to");


		if (reportingPeriodFromConfiguration.isEmpty()) {

			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.from property has been found");

			throw new BusinessException(
					MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_FROM_NOT_FOUND,
					event);
		}

		if (reportingPeriodToConfiguration.isEmpty()) {

			log.error(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.from property has been found");

			throw new BusinessException(
					MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_FROM_NOT_FOUND,
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
		}

		return false;
	}

	private void notifyRegistry(InstallationReportableEmissionsUpdatedEvent event, InstallationAccountInfoDTO account) {

		if (ObjectUtils.isEmpty(account.getRegistryId())) {
			log.info(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Cannot send emissions to ETS Registry because account doesn't have a registry id");
			notifyRegulator(event, account, PmrvNotificationTemplateName.REGISTRY_INTEGRATION_MISSING_REGISTRY_ID);
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
							installationAccountEmissionsUpdatedKafkaTemplate);

		log.info(REQUEST_LOG_FORMAT,
					SERVICE_KEY,
					event.getAccountId(),
					INTEGRATION_POINT_KEY,
					"Emissions sent to registry " + accountEmissionsUpdatedRequestEvent);
	}

	private void notifyRegulator(ReportableEmissionsUpdatedEvent event, InstallationAccountInfoDTO account,
								 PmrvNotificationTemplateName templateName) {

		final Map<String, Object> templateParams = new HashMap<>();
		templateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId());
		templateParams.put(PmrvEmailNotificationTemplateConstants.REGISTRY_ID, account.getRegistryId());
		templateParams.put(PmrvEmailNotificationTemplateConstants.ERROR_MESSAGE, MISSING_REGISTRY_ID_ERROR_MESSAGE);
		templateParams.put(PmrvEmailNotificationTemplateConstants.YEAR, event.getYear().getValue());
		templateParams.put(PmrvEmailNotificationTemplateConstants.EMISSIONS, event.getReportableEmissions());
		templateParams.put(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, capitalizeFirstLetter(SERVICE_KEY));
		templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName());

		final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.competentAuthority(account.getCompetentAuthority())
						.templateName(templateName.getName())
						.accountType(account.getAccountType())
						.templateParams(templateParams)
						.build())
				.build();
		notificationEmailService.notifyRecipient(emailData, emailProperties.getEmail().get(account.getCompetentAuthority().getCode()));
	}

}
