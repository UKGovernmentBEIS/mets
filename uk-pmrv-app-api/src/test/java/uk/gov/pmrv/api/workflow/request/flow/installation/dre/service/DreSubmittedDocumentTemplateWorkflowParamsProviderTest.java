package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFee;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.DreMonitoringApproachReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfCO2ReportingEmissions;

@ExtendWith(MockitoExtension.class)
class DreSubmittedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
	private DreSubmittedDocumentTemplateWorkflowParamsProvider cut;

	@Test
	void getContextActionType() {
		assertThat(cut.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.DRE_SUBMIT);
	}

	@Test
	void constructParams_no_fee() {
		Year reportingYear = Year.of(2023);
		AerInitiatorRequest initiatorRequest = AerInitiatorRequest.builder().type(RequestType.AER).build();
		DreFee fee = DreFee.builder()
				.chargeOperator(false)
				.build();
		Map<MonitoringApproachType, DreMonitoringApproachReportingEmissions> approachEmissions = Map.of(
				MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder()
						.reportableEmissions(BigDecimal.TEN)
						.sustainableBiomass(BigDecimal.ONE)
						.build())
				.measureTransferredCO2(true)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder()
						.reportableEmissions(BigDecimal.valueOf(3L))
						.sustainableBiomass(BigDecimal.valueOf(4L))
						.build())
				.build()
				);
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
						.build())
				.officialNoticeReason("off notice reason")
				.informationSources(Set.of("inf1", "inf2"))
				.fee(fee)
				.monitoringApproachReportingEmissions(approachEmissions)
				.build();
		DreRequestPayload payload = DreRequestPayload.builder()
				.initiatorRequest(initiatorRequest)
				.reportingYear(reportingYear)
				.dre(dre)
				.build();
		String requestId = "1";
		
		Map<String, Object> result = cut.constructParams(payload, requestId);
		
		Map<String, Object> expectedParams = new HashMap<>();
		expectedParams.put("initiatorRequest", initiatorRequest);
		expectedParams.put("reportingYear", reportingYear);
		expectedParams.put("determinationReasonDescription", DreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT.getDescription());
		expectedParams.put("officialNoticeReason", "off notice reason");
		expectedParams.put("informationSources", Set.of("inf1", "inf2"));
		expectedParams.put("chargeOperator", false);
		expectedParams.put("reportableEmissions", approachEmissions);
		expectedParams.put("totalReportableEmissions", dre.getTotalReportableEmissions());
		expectedParams.put("transferredEmissions", dre.getTotalTransferredEmissions());

		assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedParams);
	}
	
	@Test
	void constructParams_other_reason_with_fee() {
		Year reportingYear = Year.of(2023);
		LocalDate dueDate = LocalDate.now();
		AerInitiatorRequest initiatorRequest = AerInitiatorRequest.builder().type(RequestType.AER).build();
		DreFee fee = DreFee.builder()
				.chargeOperator(true)
				.feeDetails(DreFeeDetails.builder()
						.hourlyRate(BigDecimal.TEN)
						.totalBillableHours(BigDecimal.TEN)
						.dueDate(dueDate)
						.build())
				.build();
		Map<MonitoringApproachType, DreMonitoringApproachReportingEmissions> approachEmissions = Map.of(
				MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2ReportingEmissions.builder()
				.type(MonitoringApproachType.MEASUREMENT_CO2)
				.emissions(ReportableAndBiomassEmission.builder()
						.reportableEmissions(BigDecimal.TEN)
						.sustainableBiomass(BigDecimal.ONE)
						.build())
				.measureTransferredCO2(true)
				.transferredCO2Emissions(ReportableAndBiomassEmission.builder()
						.reportableEmissions(BigDecimal.valueOf(3L))
						.sustainableBiomass(BigDecimal.valueOf(4L))
						.build())
				.build()
				);
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.type(DreDeterminationReasonType.OTHER)
						.typeOtherSummary("other reason")
						.build())
				.officialNoticeReason("off notice reason")
				.informationSources(Set.of("inf1", "inf2"))
				.fee(fee)
				.monitoringApproachReportingEmissions(approachEmissions)
				.build();
		DreRequestPayload payload = DreRequestPayload.builder()
				.initiatorRequest(initiatorRequest)
				.reportingYear(reportingYear)
				.dre(dre)
				.build();
		String requestId = "1";
		
		Map<String, Object> result = cut.constructParams(payload, requestId);
		
		Map<String, Object> expectedParams = new HashMap<>();
		expectedParams.put("initiatorRequest", initiatorRequest);
		expectedParams.put("reportingYear", reportingYear);
		expectedParams.put("determinationReasonDescription", "other reason");
		expectedParams.put("officialNoticeReason", "off notice reason");
		expectedParams.put("informationSources", Set.of("inf1", "inf2"));
		expectedParams.put("chargeOperator", true);
		expectedParams.put("feeAmount", fee.getFeeAmount());
		expectedParams.put("feeDetails", fee.getFeeDetails());
		expectedParams.put("reportableEmissions", approachEmissions);
		expectedParams.put("totalReportableEmissions", dre.getTotalReportableEmissions());
		expectedParams.put("transferredEmissions", dre.getTotalTransferredEmissions());

		assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedParams);
	}
}
