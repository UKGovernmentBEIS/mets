package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.BusinessOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.IndividualOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceActivePermit;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceOrganizationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRegistryIntegrationRequestActionPayload;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(final String requestId, InstallationAccountCreatedRequestActionDTO account) {
        Request request = requestService.findRequestById(requestId);

        PermitIssuanceActivePermit permitIssuanceActivePermit =
                PermitIssuanceActivePermit.builder()
                        .emitterId(account.getEmitterId())
                        .permitId(account.getPermitId())
                        .installationName(account.getInstallationName())
                        .operatorName(account.getLegalEntityDTO().getName())
                        .regulator(account.getCompetentAuthority())
                        .regulatedActivitiesStartDate(account.getCommencementDate())
                        .build();

        PermitIssuanceOrganizationDetails permitIssuanceOrganizationDetails=
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


        PermitIssuanceRegistryIntegrationRequestActionPayload payload =
                PermitIssuanceRegistryIntegrationRequestActionPayload.builder()
                        .activePermit(permitIssuanceActivePermit)
                        .organizationDetails(permitIssuanceOrganizationDetails)
                        .payloadType(RequestActionPayloadType.PERMIT_ISSUANCE_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD)
                        .build();


        requestService.addSystemActionToRequest(request,payload, RequestActionType.PERMIT_ISSUANCE_ACCOUNT_CREATED_SENT_TO_REGISTRY);
    }

}
