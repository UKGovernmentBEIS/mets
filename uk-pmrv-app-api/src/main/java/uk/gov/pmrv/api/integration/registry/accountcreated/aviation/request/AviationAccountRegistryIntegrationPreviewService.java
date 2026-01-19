package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class AviationAccountRegistryIntegrationPreviewService {

    private final RequestService requestService;
    private final AviationAccountQueryService aviationAccountQueryService;

    @Transactional(readOnly = true)
    public AviationAccountRegistryViewDTO getAviationAccountRegistryView(String requestId) {
        Request request = requestService.findRequestById(requestId);
        RequestTask reviewTask = request.getRequestTasks().stream()
                .filter(requestTask -> requestTask.getType().equals(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW)
                || requestTask.getType().equals(RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS))
                .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) reviewTask.getPayload();
        AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOById(request.getAccountId());
        return buildAviationAccountRegistryViewDTO(aviationAccountDTO, payload);
    }

    private AviationAccountRegistryViewDTO buildAviationAccountRegistryViewDTO(AviationAccountDTO aviationAccount,EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload) {
        AviationOperatorDetails operatorDetails = AviationOperatorDetails.builder()
                .emitterId(aviationAccount.getEmitterId())
                .operatorName(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName())
                .firstYearOfReportingObligation(aviationAccount.getCommencementDate().getYear())
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
