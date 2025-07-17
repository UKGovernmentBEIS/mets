package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsBuildEmpOriginatedDataService {
	
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	private final AviationAccountQueryService aviationAccountQueryService;

	public EmpUkEtsOriginatedData build(Long accountId) {
		final Optional<EmissionsMonitoringPlanUkEtsDTO> empOpt =
                emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
		
		final AviationAccountInfoDTO accountInfo = aviationAccountQueryService
				.getAviationAccountInfoDTOById(accountId);
		
		final Optional<EmpOperatorDetails> empOperatorDetailsOpt = empOpt
                .map(EmissionsMonitoringPlanUkEtsDTO::getEmpContainer)
                .map(EmissionsMonitoringPlanUkEtsContainer::getEmissionsMonitoringPlan)
                .map(EmissionsMonitoringPlanUkEts::getOperatorDetails);
		
		final EmpUkEtsOriginatedData empUkEtsOriginatedData = EmpUkEtsOriginatedData.builder()
				.operatorDetails(AviationOperatorDetails.builder()
	                    .operatorName(accountInfo.getName())
	                    .crcoCode(accountInfo.getCrcoCode())
						.flightIdentification(
								empOperatorDetailsOpt.map(EmpOperatorDetails::getFlightIdentification).orElse(null))
						.airOperatingCertificate(
								empOperatorDetailsOpt.map(EmpOperatorDetails::getAirOperatingCertificate).orElse(null))
						.operatingLicense(
								empOperatorDetailsOpt.map(EmpOperatorDetails::getOperatingLicense).orElse(null))
						.organisationStructure(
								empOperatorDetailsOpt.map(EmpOperatorDetails::getOrganisationStructure).orElse(null))
	                    .build())
				.build();
		
		if(empOpt.isPresent()) {
			final EmissionsMonitoringPlanUkEtsContainer empContainer = empOpt.get().getEmpContainer();
			final Map<UUID, String> empOriginatedOperatorDetailsAttachments = new HashMap<>(
					empContainer.getEmpAttachments());
			empOriginatedOperatorDetailsAttachments.keySet()
					.retainAll(empContainer.getEmissionsMonitoringPlan().getOperatorDetails().getAttachmentIds());
			empUkEtsOriginatedData.setOperatorDetailsAttachments(empOriginatedOperatorDetailsAttachments);
		}
		
		return empUkEtsOriginatedData;
	}
	
}
