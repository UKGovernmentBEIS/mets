package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.BusinessOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.IndividualOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.InstallationAccountRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationAccountCreateActivePermit;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationOrganizationDetails;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(final String requestId, InstallationAccountCreatedRequestActionDTO account) {
        Request request = requestService.findRequestById(requestId);

        RegistryIntegrationAccountCreateActivePermit registryIntegrationAccountCreateActivePermit =
                RegistryIntegrationAccountCreateActivePermit.builder()
                        .emitterId(account.getEmitterId())
                        .permitId(account.getPermitId())
                        .installationName(account.getInstallationName())
                        .operatorName(account.getLegalEntityDTO().getName())
                        .firstYearOfReportingObligation(account.getRegistryReportingFirstYear())
                        .regulatedActivity(account.getRegulatedActivityList())
                        .regulator(account.getCompetentAuthority().getCode())
                        .build();

        RegistryIntegrationOrganizationDetails registryIntegrationOrganizationDetails =
                switch (account.getLegalEntityDTO().getType()) {
                     case SOLE_TRADER -> IndividualOrganisationDetails.builder()
                             .organisationLegalStatus(LegalEntityType.SOLE_TRADER)
                             .operatorAddress(account.getLegalEntityDTO().getAddress())
                             .build();
                     case LIMITED_COMPANY -> BusinessOrganisationDetails.builder()
                             .organisationLegalStatus(LegalEntityType.LIMITED_COMPANY)
                             .companyRegistrationNumber(account.getLegalEntityDTO().getReferenceNumber())
                             .justification(account.getLegalEntityDTO().getNoReferenceNumberReason())
                             .registeredAddress(account.getLegalEntityDTO().getAddress())
                             .build();
                    case PARTNERSHIP -> BusinessOrganisationDetails.builder()
                             .organisationLegalStatus(LegalEntityType.PARTNERSHIP)
                             .companyRegistrationNumber(account.getLegalEntityDTO().getReferenceNumber())
                             .justification(account.getLegalEntityDTO().getNoReferenceNumberReason())
                             .registeredAddress(account.getLegalEntityDTO().getAddress())
                             .build();
                    case OTHER -> BusinessOrganisationDetails.builder()
                            .organisationLegalStatus(LegalEntityType.OTHER)
                            .companyRegistrationNumber(account.getLegalEntityDTO().getReferenceNumber())
                            .justification(account.getLegalEntityDTO().getNoReferenceNumberReason())
                            .registeredAddress(account.getLegalEntityDTO().getAddress())
                            .build();
                };


        InstallationAccountRegistryIntegrationRequestActionPayload payload =
                InstallationAccountRegistryIntegrationRequestActionPayload.builder()
                        .activePermit(registryIntegrationAccountCreateActivePermit)
                        .organizationDetails(registryIntegrationOrganizationDetails)
                        .payloadType(RequestActionPayloadType.PERMIT_ISSUANCE_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD)
                        .build();


        requestService.addSystemActionToRequest(request,payload, RequestActionType.PERMIT_ISSUANCE_ACCOUNT_CREATED_SENT_TO_REGISTRY);
    }

}
