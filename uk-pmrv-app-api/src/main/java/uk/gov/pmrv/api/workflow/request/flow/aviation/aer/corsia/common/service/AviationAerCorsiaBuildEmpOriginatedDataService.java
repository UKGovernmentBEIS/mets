package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaBuildEmpOriginatedDataService {

	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	private final AviationAccountQueryService aviationAccountQueryService;
	
	public EmpCorsiaOriginatedData build(Long accountId) {
		final Optional<EmissionsMonitoringPlanCorsiaDTO> empOpt = emissionsMonitoringPlanQueryService
				.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
		
		final Optional<EmpCorsiaOperatorDetails> empOperatorDetailsOpt =
				empOpt.map(EmissionsMonitoringPlanCorsiaDTO::getEmpContainer)
						.map(EmissionsMonitoringPlanCorsiaContainer::getEmissionsMonitoringPlan)
						.map(EmissionsMonitoringPlanCorsia::getOperatorDetails);
		
		final EmpCorsiaOriginatedData empCorsiaOriginatedData = EmpCorsiaOriginatedData.builder()
				.operatorDetails(AviationCorsiaOperatorDetails.builder()
						.operatorName(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)
								.getName())
						.flightIdentification(empOperatorDetailsOpt
								.map(EmpCorsiaOperatorDetails::getFlightIdentification).orElse(null))
						.airOperatingCertificate(empOperatorDetailsOpt
								.map(EmpCorsiaOperatorDetails::getAirOperatingCertificate).orElse(null))
						.organisationStructure(empOperatorDetailsOpt
								.map(EmpCorsiaOperatorDetails::getOrganisationStructure).orElse(null))
						.build())
				.build();
		
		if(empOpt.isPresent()) {
			final EmissionsMonitoringPlanCorsiaContainer empContainer = empOpt.get().getEmpContainer();
			final Map<UUID, String> empOriginatedOperatorDetailsAttachments = new HashMap<>(
					empContainer.getEmpAttachments());
			empOriginatedOperatorDetailsAttachments.keySet()
					.retainAll(empContainer.getEmissionsMonitoringPlan().getOperatorDetails().getAttachmentIds());
			empCorsiaOriginatedData.setOperatorDetailsAttachments(empOriginatedOperatorDetailsAttachments);
		}
		
		return empCorsiaOriginatedData;
	}
}
