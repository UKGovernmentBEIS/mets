package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerComplianceMonitoringReportingRules;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationVerificationSubmitInitializerTest {
    
    @InjectMocks
    private AviationAerUkEtsApplicationVerificationSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
    
    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;
    
    @Test
    void initializePayload_vb_not_changed_and_saf_data_not_changed() {
        Long requestVBId = 1L;
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("name")
                .build())
            .saf(AviationAerSaf.builder().exist(Boolean.TRUE)
            		.safDetails(AviationAerSafDetails.builder()
            				.emissionsFactor(BigDecimal.TEN)
            				.build())
            		.build())
            .build();
        
        AviationAerUkEtsVerificationReport requestVerificationReport = AviationAerUkEtsVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.safExists(Boolean.TRUE)
        		.verificationData(AviationAerUkEtsVerificationData.builder()
        				.complianceMonitoringReportingRules(AviationAerComplianceMonitoringReportingRules.builder()
        						.accuracyNonCompliantReason("acc")
        						.build())
        				.emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
        						.dataSampling("sampling")
        						.build())
        				.build())
        		.build();
        
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(requestVerificationReport)
            .build();
        
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(requestPayload)
            .verificationBodyId(requestVBId)
            .accountId(accountId)
            .metadata(aerRequestMetadata)
            .build();

        String accountOperatorName = "operatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("service_contact_name").build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
                .serviceContactDetails(serviceContactDetails)
                .operatorName(accountOperatorName)
                .build();
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        
        BigDecimal totalEmissions = BigDecimal.valueOf(12588.45);
        when(aviationAerUkEtsEmissionsCalculationService.calculateTotalSubmittedEmissions(aer))
        	.thenReturn(totalEmissions);
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
            .thenReturn(Optional.of(latestVerificationBodyDetails));
       
        //invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertEquals(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload resultPayload = (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AviationAerUkEtsVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.safExists(aer.getSaf().getExist())
        		.verificationData(requestPayload.getVerificationData())
        		.build());
        assertEquals(serviceContactDetails, resultPayload.getServiceContactDetails());
        assertThat(resultPayload.getTotalEmissionsProvided()).isEqualTo(totalEmissions);
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }
    
    @Test
    void initializePayload_vb_changed_and_saf_data_changed() {
    	Long requestVBId = 1L;
        Long reportVBId = 2L;
        
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("name")
                .build())
            .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                  .aggregatedEmissionDataDetails(Set.of(
                      AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                          .airportFrom(AviationRptAirportsDTO.builder()
                              .name("Airport1")
                              .icao("ICAO1")
                              .country("Country1")
                              .countryType(CountryType.EEA_COUNTRY)
                              .build())
                          .airportTo(AviationRptAirportsDTO.builder()
                              .name("Airport2")
                              .icao("ICAO2")
                              .country("Country2")
                              .countryType(CountryType.EEA_COUNTRY)
                              .build())
                          .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                          .fuelConsumption(new BigDecimal("1000.123"))
                          .flightsNumber(10)
                          .build(),
                          AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                          .airportFrom(AviationRptAirportsDTO.builder()
                              .name("Airport3")
                              .icao("ICAO3")
                              .country("Country3")
                              .countryType(CountryType.EEA_COUNTRY)
                              .build())
                          .airportTo(AviationRptAirportsDTO.builder()
                              .name("Airport4")
                              .icao("ICAO4")
                              .country("Country4")
                              .countryType(CountryType.EEA_COUNTRY)
                              .build())
                          .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                          .fuelConsumption(new BigDecimal("1500.234"))
                          .flightsNumber(20)
                          .build()
                  ))
                  .build())
            .saf(AviationAerSaf.builder().exist(Boolean.FALSE).build())
            .build();
        
        AviationAerUkEtsVerificationReport requestVerificationReport = AviationAerUkEtsVerificationReport.builder()
        		.verificationBodyId(reportVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.safExists(Boolean.TRUE)
        		.verificationData(AviationAerUkEtsVerificationData.builder()
        				.complianceMonitoringReportingRules(AviationAerComplianceMonitoringReportingRules.builder()
        						.accuracyNonCompliantReason("acc")
        						.build())
        				.emissionsReductionClaimVerification(AviationAerEmissionsReductionClaimVerification.builder()
        						.dataSampling("sampling")
        						.build())
        				.build())
        		.build();
        
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(requestVerificationReport)
            .build();
        
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(requestPayload)
            .verificationBodyId(requestVBId)
            .accountId(accountId)
            .metadata(aerRequestMetadata)
            .build();

        String accountOperatorName = "operatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("service_contact_name").build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
                .serviceContactDetails(serviceContactDetails)
                .operatorName(accountOperatorName)
                .build();
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        
        BigDecimal totalEmissions = BigDecimal.valueOf(12588.45);
        when(aviationAerUkEtsEmissionsCalculationService.calculateTotalSubmittedEmissions(aer))
        	.thenReturn(totalEmissions);
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
            .thenReturn(Optional.of(latestVerificationBodyDetails));
       
        //invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();
        assertEquals(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload resultPayload = (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AviationAerUkEtsVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.safExists(false)
        		.verificationData(AviationAerUkEtsVerificationData.builder()
						.opinionStatement(AviationAerOpinionStatement.builder()
								.fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE)).build())
        				.build())
        		.build());
        assertEquals(serviceContactDetails, resultPayload.getServiceContactDetails());
        assertThat(resultPayload.getTotalEmissionsProvided()).isEqualTo(totalEmissions);
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }
}