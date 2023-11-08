package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaUpdateEmpService {

	private final RequestService requestService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmissionsMonitoringPlanService empService;
	private final AviationAccountUpdateService aviationAccountUpdateService;
	
	private static final EmpVariationCorsiaMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaMapper.class);
	
	@Transactional
	public void updateEmp(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
        final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        final RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        final EmissionsMonitoringPlanCorsiaContainer empContainer = MAPPER.toEmissionsMonitoringPlanCorsiaContainer(
        				requestPayload, aviationAccountInfo, EmissionTradingScheme.CORSIA);
        
        empService.updateEmissionsMonitoringPlan(accountId, empContainer);
        
        final EmpCorsiaOperatorDetails operatorDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();
        
        aviationAccountUpdateService.updateAccountUponEmpVariationApproved(
                accountId,
                operatorDetails.getOperatorName(),
                getContactLocationFromEmp(operatorDetails)
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
