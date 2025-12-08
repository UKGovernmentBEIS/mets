package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;



@Service
@RequiredArgsConstructor
public class AviationAccountRegistryIntegrationPreviewService {

    private final RequestService requestService;
    private final AviationAccountQueryService aviationAccountQueryService;

    @Transactional(readOnly = true)
    public AviationAccountRegistryViewDTO getAviationAccountRegistryView(String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOById(request.getAccountId());
        EmpIssuanceUkEtsRequestPayload payload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        return buildAviationAccountRegistryViewDTO(aviationAccountDTO, payload);
    }

    private AviationAccountRegistryViewDTO buildAviationAccountRegistryViewDTO(AviationAccountDTO aviationAccount,EmpIssuanceUkEtsRequestPayload payload) {
        AviationOperatorDetails operatorDetails = AviationOperatorDetails.builder()
                .emitterId(aviationAccount.getEmitterId())
                .operatorName(aviationAccount.getName())
                .firstKnownAviationActivity(aviationAccount.getCommencementDate())
                .regulator(aviationAccount.getCompetentAuthority().getCode())
                .build();

        OrganisationStructure structure = payload.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure();

        AviationOrganisationDetails aviationOrganisationDetails =
                switch (structure.getLegalStatusType()) {
                    case LIMITED_COMPANY -> AviationLimitedCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.LIMITED_COMPANY)
                            .registeredAddress(structure.getOrganisationLocation())
                            .companyRegistrationNumber(((LimitedCompanyOrganisation) structure).getRegistrationNumber())
                            .build();
                    case INDIVIDUAL -> AviationIndividualCompanyDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.INDIVIDUAL)
                            .fullName(((IndividualOrganisation) structure).getFullName())
                            .address(structure.getOrganisationLocation())
                            .build();
                    case PARTNERSHIP -> AviationPartnershipDetails.builder()
                            .organisationLegalStatus(OrganisationLegalStatusType.PARTNERSHIP)
                            .mainOfficeAddress(structure.getOrganisationLocation())
                            .partnershipName(((PartnershipOrganisation) structure).getPartnershipName())
                            .build();
                };

        return AviationAccountRegistryViewDTO.builder().operatorDetails(operatorDetails).organisationDetails(aviationOrganisationDetails).build();
    }

}
