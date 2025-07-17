package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerSmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationAerUkEtsApplicationSubmitInitializer cut;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Test
    void initializeTaskPayload() {
    	Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";
        
		AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder()
				.year(reportingYear).build();
        
		AviationAerUkEts aer = AviationAerUkEts.builder()
				.operatorDetails(AviationOperatorDetails.builder()
						.crcoCode("crcCode")
						.build())
	            .monitoringApproach(AviationAerSmallEmittersMonitoringApproach.builder()
	            		.monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
	            		.build())
	            .build();
		Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
	            "monitoringApproach", List.of(false));
		Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");
		EmpUkEtsOriginatedData empOriginatedData = EmpUkEtsOriginatedData.builder()
				.operatorDetails(
						AviationOperatorDetails.builder()
							.operatorName("name")
							.crcoCode("crcCode")
							.build())
				.build();
		Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
	            "verMonitoringApproach", List.of(true));
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
                .reportingRequired(Boolean.TRUE)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder()
                		.noReportingReason("noReportingReason")
                		.build()) 
                .aer(aer)
                .aerAttachments(aerAttachments)
                .aerSectionsCompleted(aerSectionsCompleted)
                .empOriginatedData(empOriginatedData)
				.aerMonitoringPlanVersions(List
						.of(createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)))
				.verificationPerformed(true)
				.verificationReport(AviationAerUkEtsVerificationReport.builder()
						.verificationBodyId(1L)
						.build())
				.verificationSectionsCompleted(verificationSectionsCompleted)
				.build();
        
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .metadata(requestMetadata)
            .payload(requestPayload)
            .accountId(accountId)
            .build();

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .email("email")
            .roleCode("role code")
            .build();
		when(accountContactQueryService.getServiceContactDetails(accountId))
				.thenReturn(Optional.of(serviceContactDetails));
		
		AviationAccountInfoDTO accountInfoDTO = AviationAccountInfoDTO.builder()
				.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
				.crcoCode("latestCrcoCode")
				.build();
		when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId))
			.thenReturn(accountInfoDTO);

        AviationAerUkEtsApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
			.sendEmailNotification(true)
            .reportingYear(reportingYear)
            .reportingRequired(Boolean.TRUE)
            .reportingObligationDetails(requestPayload.getReportingObligationDetails())
            .aer(aer)
            .aerAttachments(aerAttachments)
            .aerSectionsCompleted(aerSectionsCompleted)
            .empOriginatedData(requestPayload.getEmpOriginatedData())
            .aerMonitoringPlanVersions(requestPayload.getAerMonitoringPlanVersions())
            .serviceContactDetails(serviceContactDetails)
            .verificationBodyId(1L)
            .verificationPerformed(requestPayload.isVerificationPerformed())
            .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
            .build();

        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) cut.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
        verify(accountContactQueryService, times(1)).getServiceContactDetails(accountId);

		// Verify email set to false on exempt status
		accountInfoDTO.setReportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL);
		expectedRequestTaskPayload.setSendEmailNotification(false);
		requestTaskPayload = (AviationAerUkEtsApplicationSubmitRequestTaskPayload) cut.initializePayload(request);
		assertEquals(expectedRequestTaskPayload, requestTaskPayload);

		accountInfoDTO.setReportingStatus(AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL);
		requestTaskPayload = (AviationAerUkEtsApplicationSubmitRequestTaskPayload) cut.initializePayload(request);
		assertEquals(expectedRequestTaskPayload, requestTaskPayload);

    }

	@Test
	void initializeTaskPayloadExemptNoEmail() {

	}

    @Test
    void getRequestTaskTypes() {
        assertThat(cut.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT);
    }

    private AviationAerMonitoringPlanVersion createAerMonitoringPlanVersion(String empId, LocalDate approvalDate, Integer consolidationNumber) {
        return AviationAerMonitoringPlanVersion.builder()
            .empId(empId)
            .empApprovalDate(approvalDate)
            .empConsolidationNumber(consolidationNumber)
            .build();
    }

}