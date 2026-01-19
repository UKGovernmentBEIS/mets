package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryAccountType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.RegistryAccountHolderType;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction.AviationAccountUpdatedRequestActionService;
import uk.gov.pmrv.api.integration.registry.accountupdated.common.RegistryIntegrationEmailNotifierService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;

import java.util.Optional;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountUpdatedNotifyRegistryService {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAccountUpdatedRegistryProducer registryProducer;
    private final EmissionsMonitoringPlanQueryService empQueryService;
    private final AviationAccountUpdatedRequestActionService requestActionService;
    private final RegistryIntegrationEmailNotifierService notifierService;


    public void notifyRegistry(AviationAccountUpdatedRegistryEvent event) {

        Long accountId = event.getAccountId();
        Optional<String> monitoringPlanIdOptional = empQueryService.getEmpIdByAccountId(event.getAccountId());
        final AviationAccountDTO accountDTO = aviationAccountQueryService.getAviationAccountDTOById(accountId);
        final EmissionsMonitoringPlanUkEts emp = getEmp(event,monitoringPlanIdOptional);
        if(ObjectUtils.isEmpty(emp)) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                    NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY, "Unable to publish account updated " +
                            "event to registry. The monitoring plan id field is empty and there is no pending emp approval request");
            return;
        }
        if(ObjectUtils.isEmpty(accountDTO.getRegistryId())) {
            log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                    NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY, "Unable to publish account updated " +
                            "event to registry. The Registry/Operator Id field is empty");
            notifierService.registryIdNonExistenceNotifyRegulatorForAction(accountDTO);
            return;
        }
        AccountUpdatingEvent accountUpdatingEvent = buildAccountUpdatingPayload(accountDTO,emp,monitoringPlanIdOptional);
        registryProducer.produce(accountUpdatingEvent);
        if(!ObjectUtils.isEmpty(event.getRequestId())) {
            requestActionService.addRequestAction(event.getRequestId(),accountDTO,emp);
        }

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY, "Account updated event sent to registry ");
    }


    private AccountUpdatingEvent buildAccountUpdatingPayload(AviationAccountDTO accountDTO,EmissionsMonitoringPlanUkEts emp,Optional<String> monitoringPlanIdOptional) {

        UpdateAccountDetailsMessage updateAccountDetailsMessage = UpdateAccountDetailsMessage.builder()
                .accountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name())
                .registryId(String.valueOf(accountDTO.getRegistryId()))
                .monitoringPlanId(monitoringPlanIdOptional.orElse(null))
                .firstYearOfVerifiedEmissions(accountDTO.getCommencementDate().getYear())
                .accountName(emp.getOperatorDetails().getOperatorName())
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(accountDTO.getCompetentAuthority()))
                .build();


        return AccountUpdatingEvent.builder()
                .accountDetails(updateAccountDetailsMessage)
                .accountHolder(createAccountHolderDetails(emp))
                .build();
    }

    private record UpdateOrganisationDetails(String companyRegistrationNumber, String accountHolderName, Boolean crnNotExists, String justification) {}


    private AccountHolderMessage createAccountHolderDetails(EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts) {
        OrganisationStructure organisationStructure = emissionsMonitoringPlanUkEts
                .getOperatorDetails()
                .getOrganisationStructure();
        String operatorName = emissionsMonitoringPlanUkEts.getOperatorDetails().getOperatorName();

        UpdateOrganisationDetails details = switch (organisationStructure) {
            case LimitedCompanyOrganisation limitedCompanyOrganisation ->
                    new UpdateOrganisationDetails(limitedCompanyOrganisation.getRegistrationNumber(), operatorName, Boolean.FALSE,null);
            case IndividualOrganisation individualOrganisation ->
                    new UpdateOrganisationDetails(null, operatorName, null,null);
            case PartnershipOrganisation partnershipOrganisation ->
                    new UpdateOrganisationDetails(null, operatorName, Boolean.TRUE,"Partnership");
            default -> new UpdateOrganisationDetails(null, null, null,null);
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

    private EmissionsMonitoringPlanUkEts getEmp(AviationAccountUpdatedRegistryEvent event,Optional<String> monitoringPlanIdOptional) {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts = null;
        if(monitoringPlanIdOptional.isPresent()) {
            emissionsMonitoringPlanUkEts = ((EmissionsMonitoringPlanUkEtsContainer) empQueryService.getEmpContainerById(monitoringPlanIdOptional.get())).getEmissionsMonitoringPlan();
        }
        else {
            emissionsMonitoringPlanUkEts = event.getEmissionsMonitoringPlan();
        }
        return emissionsMonitoringPlanUkEts;
    }

}
