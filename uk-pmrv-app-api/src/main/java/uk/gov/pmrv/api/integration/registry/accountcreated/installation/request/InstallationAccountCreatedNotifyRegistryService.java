package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryRegulatedActivityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.RegistryAccountHolderType;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountPermitQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceRegistryIntegrationAddRequestActionService;

import java.util.stream.Collectors;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountCreatedNotifyRegistryService {

    private final InstallationAccountPermitQueryOrchestrator accountQueryService;
    private final InstallationAccountCreatedSendToRegistryProducer registryProducer;
    private final PermitIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;


    public void notifyRegistry(InstallationAccountCreatedRegistryEvent event) {
        Long accountId = event.getAccountId();
        final InstallationAccountPermitDTO account = accountQueryService.getAccountWithPermit(accountId);
        if (!ObjectUtils.isEmpty(account.getAccount().getRegistryId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, accountId,
                    NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                    "Cannot send account details to registry because the registry Id already exists");
            return;
        }
        AccountOpeningEvent accountOpeningEvent = buildAccountCreatedRegistryData(account);

        registryProducer.produce(accountOpeningEvent);

        addRequestActionService.addRequestAction(event.getRequestId(),buildAccountCreatedRequestActionDTO(account));

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY, "Account created event sent to registry " + accountOpeningEvent);

    }



    private AccountOpeningEvent buildAccountCreatedRegistryData(InstallationAccountPermitDTO account) {

        LegalEntityType legalEntityType = account.getAccount().getLegalEntity().getType();

        final AccountDetailsMessage accountDetailsMessage = AccountDetailsMessage.builder()
                .installationName(account.getAccount().getName())
                .emitterId(account.getAccount().getEmitterId())
                .permitId(account.getPermit().getId())
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(account.getAccount().getCompetentAuthority()))
                .accountName(account.getAccount().getName())
                .firstYearOfVerifiedEmissions(account.getAccount().getRegistryReportingFirstYear())
                .installationActivityTypes(account.getPermit().getRegulatedActivities().getRegulatedActivities().stream()
                                                .map(r -> RegistryRegulatedActivityType.getByRegulatedActivityType(r.getType()))
                                                .collect(Collectors.toSet()))
                .build();


        final AccountHolderMessage accountHolderMessage = AccountHolderMessage.builder()
                .accountHolderType(RegistryAccountHolderType.fromLegalEntityType(legalEntityType).name())
                .name(account.getAccount().getLegalEntity().getName())
                .addressLine1(account.getAccount().getLegalEntity().getAddress().getLine1())
                .addressLine2(account.getAccount().getLegalEntity().getAddress().getLine2())
                .townOrCity(account.getAccount().getLegalEntity().getAddress().getCity())
                .postalCode(account.getAccount().getLegalEntity().getAddress().getPostcode())
                .country(NotifyRegistryUtils.replaceGBCountryCode(account.getAccount().getLegalEntity().getAddress().getCountry()))
                .build();

        if (!LegalEntityType.SOLE_TRADER.equals(legalEntityType)) {
            accountHolderMessage.setCrnNotExist(ObjectUtils.isEmpty(account.getAccount().getLegalEntity().getReferenceNumber()));
            accountHolderMessage.setCompanyRegistrationNumber(account.getAccount().getLegalEntity().getReferenceNumber());
            accountHolderMessage.setCrnJustification(account.getAccount().getLegalEntity().getNoReferenceNumberReason());
        }

        return AccountOpeningEvent.builder()
                .accountType(AccountType.OPERATOR_HOLDING_ACCOUNT)
                .accountDetails(accountDetailsMessage)
                .accountHolder(accountHolderMessage)
                .build();
    }

    private InstallationAccountCreatedRequestActionDTO buildAccountCreatedRequestActionDTO(InstallationAccountPermitDTO installationAccountPermit) {
        return InstallationAccountCreatedRequestActionDTO.builder()
                .emitterId(installationAccountPermit.getAccount().getEmitterId())
                .permitId(installationAccountPermit.getPermit().getId())
                .installationName(installationAccountPermit.getAccount().getName())
                .legalEntityDTO(installationAccountPermit.getAccount().getLegalEntity())
                .competentAuthority(installationAccountPermit.getAccount().getCompetentAuthority())
                .commencementDate(installationAccountPermit.getAccount().getCommencementDate())
                .build();
    }

}
