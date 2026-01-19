package uk.gov.pmrv.api.integration.registry.accountupdated.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.BusinessOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.IndividualOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.InstallationAccountUpdatedRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationAccountUpdateActivePermit;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationOrganizationDetails;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountUpdatedAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(final String requestId, InstallationAccountPermitDTO installationAccountPermitDTO,
                                 PermitContainer permitContainer) {
        Request request = requestService.findRequestById(requestId);

        InstallationAccountDTO accountDTO = installationAccountPermitDTO.getAccount();

        RegistryIntegrationAccountUpdateActivePermit activePermit = RegistryIntegrationAccountUpdateActivePermit.builder()
                .registryId(accountDTO.getRegistryId())
                .permitId(installationAccountPermitDTO.getPermit().getId())
                .installationName(permitContainer.getInstallationOperatorDetails().getInstallationName())
                .operatorName(permitContainer.getInstallationOperatorDetails().getOperator())
                .firstYearOfReportingObligation(accountDTO.getRegistryReportingFirstYear())
                .regulatedActivity(installationAccountPermitDTO.getPermit().getRegulatedActivities().getRegulatedActivities().stream().map(RegulatedActivity::getType).collect(Collectors.toList()))
                .build();

        RegistryIntegrationOrganizationDetails registryIntegrationOrganizationDetails =
                switch (permitContainer.getInstallationOperatorDetails().getOperatorType()) {
                    case SOLE_TRADER -> IndividualOrganisationDetails.builder()
                            .organisationLegalStatus(LegalEntityType.SOLE_TRADER)
                            .operatorAddress(permitContainer.getInstallationOperatorDetails().getOperatorDetailsAddress())
                            .build();
                    case LIMITED_COMPANY -> BusinessOrganisationDetails.builder()
                            .organisationLegalStatus(LegalEntityType.LIMITED_COMPANY)
                            .companyRegistrationNumber(permitContainer.getInstallationOperatorDetails().getCompanyReferenceNumber())
                            .justification(accountDTO.getLegalEntity().getNoReferenceNumberReason())
                            .registeredAddress(permitContainer.getInstallationOperatorDetails().getOperatorDetailsAddress())
                            .build();
                    case PARTNERSHIP -> BusinessOrganisationDetails.builder()
                            .organisationLegalStatus(LegalEntityType.PARTNERSHIP)
                            .companyRegistrationNumber(permitContainer.getInstallationOperatorDetails().getCompanyReferenceNumber())
                            .justification(accountDTO.getLegalEntity().getNoReferenceNumberReason())
                            .registeredAddress(permitContainer.getInstallationOperatorDetails().getOperatorDetailsAddress())
                            .build();
                    case OTHER -> BusinessOrganisationDetails.builder()
                            .organisationLegalStatus(LegalEntityType.OTHER)
                            .companyRegistrationNumber(permitContainer.getInstallationOperatorDetails().getCompanyReferenceNumber())
                            .justification(accountDTO.getLegalEntity().getNoReferenceNumberReason())
                            .registeredAddress(permitContainer.getInstallationOperatorDetails().getOperatorDetailsAddress())
                            .build();
                };

        InstallationAccountUpdatedRegistryIntegrationRequestActionPayload payload =
                InstallationAccountUpdatedRegistryIntegrationRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_VARIATION_REGISTRY_INTEGRATION_ACCOUNT_UPDATED_PAYLOAD)
                        .activePermit(activePermit)
                        .organizationDetails(registryIntegrationOrganizationDetails)
                        .build();


        requestService.addSystemActionToRequest(request,payload, RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY);

    }

}
