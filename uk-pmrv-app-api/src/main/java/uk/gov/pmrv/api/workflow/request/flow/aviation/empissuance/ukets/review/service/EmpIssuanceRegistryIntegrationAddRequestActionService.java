package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuancePartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceRegistryIntegrationRequestActionPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(final String requestId, AviationAccountCreatedRequestActionDTO requestActionDTO) {
        Request request = requestService.findRequestById(requestId);

        EmpIssuanceOperatorDetails empIssuanceOperatorDetails = EmpIssuanceOperatorDetails.builder()
                .emitterId(requestActionDTO.getEmitterId())
                .emissionsPlanId(requestActionDTO.getPermitId())
                .operatorName(requestActionDTO.getOperatorName())
                .firstKnownAviationActivity(requestActionDTO.getFirstKnownAviationActivity())
                .regulator(requestActionDTO.getCompetentAuthority())
                .build();

        EmpIssuanceOrganisationDetails empIssuanceOrganisationDetails =
                switch (requestActionDTO.getOrganisationStructure().getLegalStatusType()) {
                    case LIMITED_COMPANY -> EmpIssuanceLimitedCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.LIMITED_COMPANY)
                            .registeredAddress(requestActionDTO.getOrganisationStructure().getOrganisationLocation())
                            .companyRegistrationNumber(((LimitedCompanyOrganisation) requestActionDTO.getOrganisationStructure()).getRegistrationNumber())
                            .build();
                    case INDIVIDUAL -> EmpIssuanceIndividualCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.INDIVIDUAL)
                            .fullName(((IndividualOrganisation) requestActionDTO.getOrganisationStructure()).getFullName())
                            .address(requestActionDTO.getOrganisationStructure().getOrganisationLocation())
                            .build();
                    case PARTNERSHIP -> EmpIssuancePartnershipDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.PARTNERSHIP)
                            .mainOfficeAddress(requestActionDTO.getOrganisationStructure().getOrganisationLocation())
                            .partnershipName(((PartnershipOrganisation) requestActionDTO.getOrganisationStructure()).getPartnershipName())
                            .build();
                };

        EmpIssuanceRegistryIntegrationRequestActionPayload payload =
                EmpIssuanceRegistryIntegrationRequestActionPayload.builder()
                        .operatorDetails(empIssuanceOperatorDetails)
                        .organisationDetails(empIssuanceOrganisationDetails)
                        .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD)
                        .build();

        requestService.addSystemActionToRequest(request,payload,RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY);
    }
}
