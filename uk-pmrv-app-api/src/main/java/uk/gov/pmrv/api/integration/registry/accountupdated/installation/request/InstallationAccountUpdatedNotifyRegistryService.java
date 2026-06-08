package uk.gov.pmrv.api.integration.registry.accountupdated.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountType;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryRegulatedActivityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.RegistryAccountHolderType;
import uk.gov.pmrv.api.integration.registry.accountupdated.common.RegistryIntegrationEmailNotifierService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;

import java.util.stream.Collectors;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountUpdatedNotifyRegistryService {

    private final InstallationAccountQueryOrchestrator accountQueryService;
    private final PermitQueryService permitQueryService;
    private final InstallationAccountUpdatedRegistryProducer registryProducer;
    private final InstallationAccountUpdatedAddRequestActionService addRequestActionService;
    private final RegistryIntegrationEmailNotifierService notifierService;

    public void notifyRegistry(InstallationAccountUpdatedRegistryEvent event) {

        Long accountId = event.getAccountId();
        final InstallationAccountPermitDTO installationAccountPermitDTO = accountQueryService.getAccountWithPermit(accountId);
        final InstallationAccountDTO account = installationAccountPermitDTO.getAccount();
        PermitContainer permitContainer;
        try {
            permitContainer = permitQueryService.getPermitContainerByAccountId(account.getId());
        } catch (BusinessException e) {
            log.info("Permit not found for account with id {} and therefore the registry update proceess" +
                    "cannot move forward",accountId);
            return;
        }
        if(!EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(account.getEmissionTradingScheme()) ||
                !PermitType.GHGE.equals(permitContainer.getPermitType())) {
            return;
        }
        if(ObjectUtils.isEmpty(account.getRegistryId())) {
            log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getAccountId(),
                    NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY, "Unable to publish account updated " +
                            "event to registry. The Registry/Operator Id field is empty");
            notifierService.registryIdNonExistenceNotifyRegulatorForAction(account);
            return;
        }
        AccountUpdatingEvent accountUpdatingEvent = buildAccountUpdatedPayload(installationAccountPermitDTO, permitContainer);
        registryProducer.produce(accountUpdatingEvent);
        if(!event.isSkipRequestAction()) {
            addRequestActionService.addRequestAction(event.getRequestId(),installationAccountPermitDTO,permitContainer);
        }

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY, "Account updated event sent to registry ");
    }


    public AccountUpdatingEvent buildAccountUpdatedPayload(InstallationAccountPermitDTO accountPermitDTO,PermitContainer permit) {
        InstallationAccountDTO account = accountPermitDTO.getAccount();
        InstallationOperatorDetails installationOperatorDetails = permit.getInstallationOperatorDetails();

        UpdateAccountDetailsMessage updateAccountDetailsMessage = UpdateAccountDetailsMessage.builder()
                .installationName(installationOperatorDetails.getInstallationName())
                .accountType(AccountType.OPERATOR_HOLDING_ACCOUNT.toString())
                .accountName(installationOperatorDetails.getOperator())
                .permitId(accountPermitDTO.getPermit().getId())
                .registryId(String.valueOf(account.getRegistryId()))
                .firstYearOfVerifiedEmissions(account.getRegistryReportingFirstYear())
                .installationActivityTypes(accountPermitDTO.getPermit().getRegulatedActivities().getRegulatedActivities().stream()
                        .map(r -> RegistryRegulatedActivityType.getByRegulatedActivityType(r.getType()))
                        .collect(Collectors.toSet()))
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(account.getCompetentAuthority()))
                .build();

        final AccountHolderMessage accountHolderMessage = AccountHolderMessage.builder()
                .accountHolderType(RegistryAccountHolderType.fromLegalEntityType(account.getLegalEntity().getType()).name())
                .name(account.getLegalEntity().getName())
                .addressLine1(installationOperatorDetails.getOperatorDetailsAddress().getLine1())
                .addressLine2(installationOperatorDetails.getOperatorDetailsAddress().getLine2())
                .townOrCity(installationOperatorDetails.getOperatorDetailsAddress().getCity())
                .postalCode(installationOperatorDetails.getOperatorDetailsAddress().getPostcode())
                .country(NotifyRegistryUtils.replaceGBCountryCode(installationOperatorDetails.getOperatorDetailsAddress().getCountry()))
                .build();

        if (!LegalEntityType.SOLE_TRADER.equals(account.getLegalEntity().getType())) {
            accountHolderMessage.setCrnNotExist(ObjectUtils.isEmpty(account.getLegalEntity().getReferenceNumber()));
            accountHolderMessage.setCompanyRegistrationNumber(installationOperatorDetails.getCompanyReferenceNumber());
            accountHolderMessage.setCrnJustification(ObjectUtils.isEmpty(installationOperatorDetails.getCompanyReferenceNumber())
                    ? account.getLegalEntity().getNoReferenceNumberReason() : null);
        }

        return AccountUpdatingEvent.builder()
                .accountDetails(updateAccountDetailsMessage)
                .accountHolder(accountHolderMessage)
                .build();
    }

}
