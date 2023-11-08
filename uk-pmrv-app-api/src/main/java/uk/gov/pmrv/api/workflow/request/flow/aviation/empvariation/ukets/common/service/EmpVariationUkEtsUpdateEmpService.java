package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsUpdateEmpService {

	private final RequestService requestService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmissionsMonitoringPlanService empService;
	private final AviationAccountUpdateService aviationAccountUpdateService;
	
	private static final EmpVariationUkEtsMapper EMP_VARIATION_UKETS_MAPPER = Mappers.getMapper(EmpVariationUkEtsMapper.class);
	
	@Transactional
	public void updateEmp(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        final RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        final EmissionsMonitoringPlanUkEtsContainer empContainer =
        		EMP_VARIATION_UKETS_MAPPER.toEmissionsMonitoringPlanUkEtsContainer(
        				requestPayload, aviationAccountInfo, EmissionTradingScheme.UK_ETS_AVIATION);
        
        empService.updateEmissionsMonitoringPlan(accountId, empContainer);
        
        final EmpOperatorDetails empOperatorDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();
        
        aviationAccountUpdateService.updateAccountUponEmpVariationApproved(
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
