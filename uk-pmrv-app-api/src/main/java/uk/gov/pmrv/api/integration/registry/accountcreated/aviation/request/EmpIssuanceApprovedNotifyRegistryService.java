package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryAccountType;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceRegistryIntegrationAddRequestActionService;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class EmpIssuanceApprovedNotifyRegistryService {

    private final AviationAccountEmpQueryOrchestrator queryOrchestrator;
    private final AviationEmpApprovedSendToRegistryProducer registryProducer;
    private final EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;
    private final EmissionsMonitoringPlanQueryService empQueryService;


    public void notifyRegistry(EmpIssuanceApprovedEvent event) {
        Long accountId = event.getAccountId();
        final AviationAccountEmpDTO aviationAccount = queryOrchestrator.getAviationAccountWithEMP(accountId);
        final EmissionsMonitoringPlanUkEtsContainer container = (EmissionsMonitoringPlanUkEtsContainer)
                empQueryService.getEmpContainerById(aviationAccount.getEmp().getId());

        if (!ObjectUtils.isEmpty(aviationAccount.getAviationAccount().getRegistryId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, accountId,
                    NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                    "Cannot send account details to registry because the registry Id already exists");
            return;
        }

        AviationAccountCreatedRegistryDTO registryData = buildAccountCreatedRegistryData(aviationAccount,container);

        registryProducer.produce(registryData);

        addRequestActionService.addRequestAction(event.getRequestId(),buildAccountCreatedRequestActionDTO(aviationAccount,container));

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY, "Account created event sent to registry " + registryData);
    }

    private AviationAccountCreatedRegistryDTO buildAccountCreatedRegistryData(AviationAccountEmpDTO aviationAccount,
                                                                              EmissionsMonitoringPlanUkEtsContainer container) {

        AviationAccountCreatedRegistryDetails registryDetails = AviationAccountCreatedRegistryDetails.builder()
                .accountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
                .emitterId(aviationAccount.getAviationAccount().getEmitterId())
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(aviationAccount.getAviationAccount().getCompetentAuthority()))
                .firstYearOfVerifiedEmissions(Math.max(aviationAccount.getAviationAccount().getCommencementDate().getYear(), 2021))
                .accountName(aviationAccount.getAviationAccount().getName())
                .monitoringPlanId(aviationAccount.getEmp().getId())
                .build();

        return AviationAccountCreatedRegistryDTO.builder().accountCreatedRegistryDetails(registryDetails)
                .aviationAccountCreatedRegistryHolderDetails(createRegistryHolderDetails(aviationAccount,container)).build();
    }

    private record OrganisationDetails(String companyRegistrationNumber, String organisationName, String individualName, Boolean crnNotExists, String justification) {}

    private AviationAccountCreatedRegistryHolderDetails createRegistryHolderDetails(AviationAccountEmpDTO aviationAccount , EmissionsMonitoringPlanUkEtsContainer container) {
        OrganisationStructure organisationStructure = container
                .getEmissionsMonitoringPlan()
                .getOperatorDetails()
                .getOrganisationStructure();

        OrganisationDetails details = switch (organisationStructure) {
            case LimitedCompanyOrganisation limitedCompanyOrganisation ->
                    new OrganisationDetails(limitedCompanyOrganisation.getRegistrationNumber(), aviationAccount.getAviationAccount().getName(), null,null,null);
            case IndividualOrganisation individualOrganisation ->
                    new OrganisationDetails(null, null, individualOrganisation.getFullName(), null,null);
            case PartnershipOrganisation partnershipOrganisation ->
                    new OrganisationDetails(null, partnershipOrganisation.getPartnershipName(), null,Boolean.TRUE,"Partnership");
            default -> new OrganisationDetails(null, null, null,null,null);
        };

        LocationOnShoreStateDTO organisationLocation = organisationStructure.getOrganisationLocation(); // readable + avoids method-repeat
        String country = NotifyRegistryUtils.replaceGBCountryCode(organisationLocation.getCountry());

        return AviationAccountCreatedRegistryHolderDetails.builder()
                .accountHolderType(OrganisationLegalStatusType.INDIVIDUAL.equals(organisationStructure.getLegalStatusType()) ? "Individual" : "Organisation")
                .organisationName(details.organisationName())
                .companyRegistrationNumber(details.companyRegistrationNumber())
                .individualName(details.individualName())
                .addressLine1(organisationLocation.getLine1())
                .addressLine2(organisationLocation.getLine2())
                .townOrCity(organisationLocation.getCity())
                .stateOrProvince(organisationLocation.getState())
                .postalCode(organisationLocation.getPostcode())
                .country(country)
                .build();
    }

    private AviationAccountCreatedRequestActionDTO buildAccountCreatedRequestActionDTO(AviationAccountEmpDTO aviationAccount,EmissionsMonitoringPlanUkEtsContainer container) {

        return AviationAccountCreatedRequestActionDTO.builder()
                .operatorName(aviationAccount.getAviationAccount().getName())
                .organisationStructure(container.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure())
                .firstKnownAviationActivity(aviationAccount.getAviationAccount().getCommencementDate())
                .emitterId(aviationAccount.getAviationAccount().getEmitterId())
                .permitId(aviationAccount.getEmp().getId())
                .competentAuthority(aviationAccount.getAviationAccount().getCompetentAuthority())
                .build();
    }

}
