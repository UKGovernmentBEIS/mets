package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;


@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountUpdatedRequestActionService {

    private final RequestService requestService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    public void addRequestAction(final String requestId , AviationAccountDTO accountDTO , EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts) {


        Request request = requestService.findRequestById(requestId);

        AviationUpdateOperatorDetails aviationOperatorDetails = AviationUpdateOperatorDetails.builder()
                .registryId(accountDTO.getRegistryId())
                .emissionsPlanId(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountDTO.getId()).orElse(null))
                .operatorName(accountDTO.getName())
                .firstYearOfReportingObligation(accountDTO.getCommencementDate().getYear())
                .build();


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

        EmpVariationRegistryIntegrationRequestActionPayload payload =
                EmpVariationRegistryIntegrationRequestActionPayload.builder()
                        .operatorDetails(aviationOperatorDetails)
                        .organisationDetails(aviationOrganisationDetails)
                        .payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_REGISTRY_INTEGRATION_ACCOUNT_UPDATED_PAYLOAD)
                        .build();


        requestService.addSystemActionToRequest(request,payload,RequestActionType.EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY);

    }
}
