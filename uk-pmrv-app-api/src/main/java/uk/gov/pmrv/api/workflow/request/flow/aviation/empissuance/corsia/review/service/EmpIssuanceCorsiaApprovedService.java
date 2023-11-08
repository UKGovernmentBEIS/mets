package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.mapper.EmpCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaApprovedService {

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;
    private final AviationAccountUpdateService aviationAccountUpdateService;

    private static final EmpCorsiaMapper EMP_CORSIA_MAPPER = Mappers.getMapper(EmpCorsiaMapper.class);

    public void approveEmp(String requestId) {
        Request request = requestService.findRequestById(requestId);
        EmpIssuanceCorsiaRequestPayload requestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer =
            EMP_CORSIA_MAPPER.toEmissionsMonitoringPlanCorsiaContainer(requestPayload, aviationAccountInfo, EmissionTradingScheme.CORSIA);

        emissionsMonitoringPlanService.submitEmissionsMonitoringPlan(accountId, empCorsiaContainer);

        EmpCorsiaOperatorDetails empOperatorDetails = empCorsiaContainer.getEmissionsMonitoringPlan().getOperatorDetails();

        aviationAccountUpdateService.updateAccountUponEmpApproved(
            accountId,
            empOperatorDetails.getOperatorName(),
            getContactLocationFromEmp(empOperatorDetails)
        );

    }

    private LocationOnShoreStateDTO getContactLocationFromEmp(EmpCorsiaOperatorDetails empOperatorDetails) {
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
