package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.mapper.EmpUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsApprovedService {

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;
    private final AviationAccountUpdateService aviationAccountUpdateService;

    private static final EmpUkEtsMapper EMP_UKETS_MAPPER = Mappers.getMapper(EmpUkEtsMapper.class);

    public void approveEmp(String requestId) {
        Request request = requestService.findRequestById(requestId);
        EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer =
            EMP_UKETS_MAPPER.toEmissionsMonitoringPlanUkEtsContainer(requestPayload, aviationAccountInfo, EmissionTradingScheme.UK_ETS_AVIATION);

        emissionsMonitoringPlanService.submitEmissionsMonitoringPlan(accountId, empUkEtsContainer);

        EmpOperatorDetails empOperatorDetails = empUkEtsContainer.getEmissionsMonitoringPlan().getOperatorDetails();

        aviationAccountUpdateService.updateAccountUponEmpApproved(
            accountId,
            empOperatorDetails.getOperatorName(),
            getContactLocationFromEmp(empOperatorDetails)
        );

    }

    private LocationOnShoreStateDTO getContactLocationFromEmp(EmpOperatorDetails empOperatorDetails) {
        OrganisationStructure organisationStructure = empOperatorDetails.getOrganisationStructure();

        if(OrganisationLegalStatusType.LIMITED_COMPANY.equals(organisationStructure.getLegalStatusType())) {

            LimitedCompanyOrganisation limitedCompanyOrganisation = (LimitedCompanyOrganisation) organisationStructure;

            if(Boolean.TRUE.equals(limitedCompanyOrganisation.getDifferentContactLocationExist())) {
                return limitedCompanyOrganisation.getDifferentContactLocation();
            }
        }

        return organisationStructure.getOrganisationLocation();
    }
}
