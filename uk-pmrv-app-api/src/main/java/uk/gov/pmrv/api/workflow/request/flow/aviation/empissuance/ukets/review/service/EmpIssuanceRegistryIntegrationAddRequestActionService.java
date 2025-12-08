package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceRegistryIntegrationRequestActionPayload;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpIssuanceRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    private final AviationAccountQueryService aviationAccountQueryService;
    private final EmissionsMonitoringPlanQueryService empQueryService;

    public void addRequestAction(AviationAccountCreatedRegistryEvent aviationAccountCreatedRegistryEvent) {
        final AviationAccountDTO accountDTO= aviationAccountQueryService.getAviationAccountDTOById(aviationAccountCreatedRegistryEvent.getAccountId());
        final Optional<EmpDetailsDTO> empDetailsDTO = empQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(aviationAccountCreatedRegistryEvent.getAccountId());


        Request request = requestService.findRequestById(aviationAccountCreatedRegistryEvent.getRequestId());

        AviationOperatorDetails aviationOperatorDetails = AviationOperatorDetails.builder()
                .emitterId(accountDTO.getEmitterId())
                .emissionsPlanId(empDetailsDTO.map(EmpDetailsDTO::getId).orElse(null))
                .operatorName(accountDTO.getName())
                .firstKnownAviationActivity(accountDTO.getCommencementDate())
                .regulator(accountDTO.getCompetentAuthority().getCode())
                .build();

        EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts = aviationAccountCreatedRegistryEvent.getEmissionsMonitoringPlan();

        AviationOrganisationDetails aviationOrganisationDetails =
                switch (emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure().getLegalStatusType()) {
                    case LIMITED_COMPANY -> AviationLimitedCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.LIMITED_COMPANY)
                            .registeredAddress(emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure().getOrganisationLocation())
                            .companyRegistrationNumber(((LimitedCompanyOrganisation) emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure()).getRegistrationNumber())
                            .build();
                    case INDIVIDUAL -> AviationIndividualCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.INDIVIDUAL)
                            .fullName(((IndividualOrganisation) emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure()).getFullName())
                            .address(emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure().getOrganisationLocation())
                            .build();
                    case PARTNERSHIP -> AviationPartnershipDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.PARTNERSHIP)
                            .mainOfficeAddress(emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure().getOrganisationLocation())
                            .partnershipName(((PartnershipOrganisation) emissionsMonitoringPlanUkEts.getOperatorDetails().getOrganisationStructure()).getPartnershipName())
                            .build();
                };

        EmpIssuanceRegistryIntegrationRequestActionPayload payload =
                EmpIssuanceRegistryIntegrationRequestActionPayload.builder()
                        .operatorDetails(aviationOperatorDetails)
                        .organisationDetails(aviationOrganisationDetails)
                        .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_REGISTRY_INTEGRATION_ACCOUNT_CREATED_PAYLOAD)
                        .build();

        if (!ObjectUtils.isEmpty(aviationAccountCreatedRegistryEvent.getAppUser())) {
            requestService.addActionToRequest(request,payload,RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY,aviationAccountCreatedRegistryEvent.getAppUser().getUserId());
        }
        else {
            requestService.addSystemActionToRequest(request,payload,RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY);
        }
    }
}
