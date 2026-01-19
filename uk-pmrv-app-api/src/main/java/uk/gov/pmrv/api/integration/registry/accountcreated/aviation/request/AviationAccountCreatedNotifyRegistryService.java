package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountType;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.RegistryAccountHolderType;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceRegistryIntegrationAddRequestActionService;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountCreatedNotifyRegistryService {

    private final AviationAccountEmpQueryOrchestrator queryOrchestrator;
    private final AviationEmpApprovedSendToRegistryProducer registryProducer;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;

    public void notifyRegistry(AviationAccountCreatedRegistryEvent event) {
        Long accountId = event.getAccountId();
        final AviationAccountEmpDTO aviationAccount = queryOrchestrator.getAviationAccountWithEMP(accountId);

        if (!ObjectUtils.isEmpty(aviationAccount.getAviationAccount().getRegistryId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, accountId,
                    NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                    "Registry ID already exists at account open. Notifying registry with account update");
            applicationEventPublisher.publishEvent(AviationAccountUpdatedRegistryEvent.builder()
                    .accountId(event.getAccountId()).requestId(event.getRequestId()).build());
            return;
        }

        AccountOpeningEvent registryData = buildAccountCreatedRegistryData(aviationAccount,event.getEmissionsMonitoringPlan());

        registryProducer.produce(registryData);

        addRequestActionService.addRequestAction(event);


        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY, "Account created event sent to registry " + registryData);
    }

    private AccountOpeningEvent buildAccountCreatedRegistryData(AviationAccountEmpDTO aviationAccount,
                                                                              EmissionsMonitoringPlanUkEts container) {

        AccountDetailsMessage registryDetails = AccountDetailsMessage.builder()
                .emitterId(aviationAccount.getAviationAccount().getEmitterId())
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(aviationAccount.getAviationAccount().getCompetentAuthority()))
                .firstYearOfVerifiedEmissions(aviationAccount.getAviationAccount().getCommencementDate().getYear())
                .accountName(container.getOperatorDetails().getOperatorName())
                .monitoringPlanId(aviationAccount.getEmp() != null ? aviationAccount.getEmp().getId() : null)
                .build();

        return AccountOpeningEvent.builder().accountType(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT).accountDetails(registryDetails)
                .accountHolder(createRegistryHolderDetails(container)).build();
    }

    private record OrganisationDetails(String companyRegistrationNumber, String accountHolderName, Boolean crnNotExists, String justification) {}

    private AccountHolderMessage createRegistryHolderDetails(EmissionsMonitoringPlanUkEts container) {
        OrganisationStructure organisationStructure = container
                .getOperatorDetails()
                .getOrganisationStructure();
        String operatorName = container.getOperatorDetails().getOperatorName();

        OrganisationDetails details = switch (organisationStructure) {
            case LimitedCompanyOrganisation limitedCompanyOrganisation ->
                    new OrganisationDetails(limitedCompanyOrganisation.getRegistrationNumber(), operatorName, Boolean.FALSE, null);
            case IndividualOrganisation individualOrganisation ->
                    new OrganisationDetails(null, operatorName , null,null);
            case PartnershipOrganisation partnershipOrganisation ->
                    new OrganisationDetails(null, operatorName, Boolean.TRUE,"Partnership");
            default -> new OrganisationDetails(null,  null,null,null);
        };

        LocationOnShoreStateDTO organisationLocation = organisationStructure.getOrganisationLocation();
        String country = NotifyRegistryUtils.replaceGBCountryCode(organisationLocation.getCountry());

        return AccountHolderMessage.builder()
                .accountHolderType(RegistryAccountHolderType.fromLegalStatusType(organisationStructure.getLegalStatusType()).name())
                .name(operatorName)
                .crnNotExist(details.crnNotExists())
                .crnJustification(details.justification())
                .companyRegistrationNumber(details.companyRegistrationNumber())
                .addressLine1(organisationLocation.getLine1())
                .addressLine2(organisationLocation.getLine2())
                .townOrCity(organisationLocation.getCity())
                .stateOrProvince(organisationLocation.getState())
                .postalCode(organisationLocation.getPostcode())
                .country(country)
                .build();
    }

}
