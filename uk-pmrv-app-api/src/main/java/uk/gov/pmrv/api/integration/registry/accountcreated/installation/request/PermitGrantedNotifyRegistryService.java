package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryAccountType;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.RegistryRegulatedActivityType;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.permit.domain.event.PermitGrantedEvent;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountPermitQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceRegistryIntegrationAddRequestActionService;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class PermitGrantedNotifyRegistryService {

    private final InstallationAccountPermitQueryOrchestrator accountQueryService;
    private final InstallationPermitGrantedSendToRegistryProducer registryProducer;
    private final PermitIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;


    public void notifyRegistry(PermitGrantedEvent event) {
        Long accountId = event.getAccountId();
        final InstallationAccountPermitDTO account = accountQueryService.getAccountWithPermit(accountId);
        if (!ObjectUtils.isEmpty(account.getAccount().getRegistryId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, accountId,
                    NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                    "Cannot send account details to registry because the registry Id already exists");
            return;
        }
        InstallationAccountCreatedRegistryDTO registryData = buildAccountCreatedRegistryData(account);

        registryProducer.produce(registryData);

        addRequestActionService.addRequestAction(event.getRequestId(),buildAccountCreatedRequestActionDTO(account));

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY, "Account created event sent to registry " + registryData);

    }



    private InstallationAccountCreatedRegistryDTO buildAccountCreatedRegistryData(InstallationAccountPermitDTO account) {

        LegalEntityType legalEntityType = account.getAccount().getLegalEntity().getType();

        final InstallationAccountCreatedRegistryDetails accountDetailsMessage = InstallationAccountCreatedRegistryDetails.builder()
                .accountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .installationName(account.getAccount().getName())
                .emitterId(account.getAccount().getEmitterId())
                .permitId(account.getPermit().getId())
                .regulator(NotifyRegistryUtils.toRegistryRegionCode(account.getAccount().getCompetentAuthority()))
                .accountName(account.getAccount().getName())
                .installationActivityTypes(account.getPermit().getRegulatedActivities().getRegulatedActivities().stream()
                        .map(r -> RegistryRegulatedActivityType.getByRegulatedActivityType(r.getType()))
                        .toList())
                .build();


        final InstallationAccountCreatedRegistryHolderDetails accountHolderMessage = InstallationAccountCreatedRegistryHolderDetails.builder()
                .accountHolderType(LegalEntityType.SOLE_TRADER.equals(legalEntityType) ? "Individual" : "Organisation")
                .organisationName(!LegalEntityType.SOLE_TRADER.equals(legalEntityType) ? account.getAccount().getLegalEntity().getName() : null)
                .individualName(LegalEntityType.SOLE_TRADER.equals(legalEntityType) ? account.getAccount().getLegalEntity().getName() : null)
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

        return InstallationAccountCreatedRegistryDTO.builder()
                .accountCreatedRegistryDetails(accountDetailsMessage)
                .accountCreatedRegistryHolderDetails(accountHolderMessage)
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
