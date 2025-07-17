package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

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
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaGeneralInformation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplicationVerificationSubmitInitializerTest {

    @InjectMocks
    private AviationAerCorsiaApplicationVerificationSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
    
    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void initializePayload_vb_not_changed() {
        Long requestVBId = 1L;
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("name")
                .build())
            .build();
        
        AviationAerCorsiaVerificationReport requestVerificationReport = AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(AviationAerCorsiaVerificationData.builder()
        				.generalInformation(AviationAerCorsiaGeneralInformation.builder()
        						.operatorData("operatorData")
        						.build())
        				.build())
        		.build();
        
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(requestVerificationReport)
            .build();
        
        AviationAerCorsiaRequestMetadata aerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
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
        
        AviationAerCorsiaTotalEmissions totalEmissions = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsEmissions(BigDecimal.valueOf(12588.45))
                .offsetFlightsEmissions(BigDecimal.valueOf(1500.234))
                .build();
        when(aviationAerCorsiaEmissionsCalculationService.calculateTotalSubmittedEmissions(aer, Year.of(2022)))
        	.thenReturn(totalEmissions);
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
            .thenReturn(Optional.of(latestVerificationBodyDetails));
       
        //invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload resultPayload = (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(requestPayload.getVerificationData())
        		.build());
        assertEquals(serviceContactDetails, resultPayload.getServiceContactDetails());
        assertThat(resultPayload.getTotalEmissionsProvided()).isEqualTo(totalEmissions.getAllFlightsEmissions());
        assertThat(resultPayload.getTotalOffsetEmissionsProvided()).isEqualTo(totalEmissions.getOffsetFlightsEmissions());
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer, Year.of(2022));
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }
    
    @Test
    void initializePayload_vb_changed() {
        Long requestVBId = 1L;
        Long reportVBId = 2L;
        
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("name")
                .build())
            .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                  .aggregatedEmissionDataDetails(Set.of(
                      AviationAerCorsiaAggregatedEmissionDataDetails.builder()
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
                          .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                          .fuelConsumption(new BigDecimal("1000.123"))
                          .flightsNumber(10)
                          .build(),
                      AviationAerCorsiaAggregatedEmissionDataDetails.builder()
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
                          .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                          .fuelConsumption(new BigDecimal("1500.234"))
                          .flightsNumber(20)
                          .build()
                  ))
                  .build())
            .build();
        
        AviationAerCorsiaVerificationReport requestVerificationReport = AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(reportVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(AviationAerCorsiaVerificationData.builder()
        				.generalInformation(AviationAerCorsiaGeneralInformation.builder()
        						.operatorData("operatorData")
        						.build())
        				.build())
        		.build();
        
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(requestVerificationReport)
            .build();
        
        AviationAerCorsiaRequestMetadata aerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
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
        
        AviationAerCorsiaTotalEmissions totalEmissions = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsEmissions(BigDecimal.valueOf(12588.45))
                .offsetFlightsEmissions(BigDecimal.valueOf(1500.234))
                .build();
        when(aviationAerCorsiaEmissionsCalculationService.calculateTotalSubmittedEmissions(aer, Year.of(2022)))
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
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload resultPayload = (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(AviationAerCorsiaVerificationData.builder()
						.opinionStatement(AviationAerCorsiaOpinionStatement.builder()
								.fuelTypes(Set.of(AviationAerCorsiaFuelType.JET_KEROSENE)).build())
        				.build())
        		.build());
        assertEquals(serviceContactDetails, resultPayload.getServiceContactDetails());
        assertThat(resultPayload.getTotalEmissionsProvided()).isEqualTo(totalEmissions.getAllFlightsEmissions());
        assertThat(resultPayload.getTotalOffsetEmissionsProvided()).isEqualTo(totalEmissions.getOffsetFlightsEmissions());
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer, Year.of(2022));
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }
    
    @Test
    void initializePayload_emission_reduction_claim_reset() {
        Long requestVBId = 1L;
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("name")
                .build())
            .build();
        
        AviationAerCorsiaVerificationReport requestVerificationReport = AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(AviationAerCorsiaVerificationData.builder()
        				.generalInformation(AviationAerCorsiaGeneralInformation.builder()
        						.operatorData("operatorData")
        						.build())
        				.emissionsReductionClaimVerification(AviationAerCorsiaEmissionsReductionClaimVerification.builder()
        						.reviewResults("review")
        						.build())
        				.build())
        		.build();
        
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(requestVerificationReport)
            .build();
        
        AviationAerCorsiaRequestMetadata aerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
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
        
        AviationAerCorsiaTotalEmissions totalEmissions = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsEmissions(BigDecimal.valueOf(12588.45))
                .offsetFlightsEmissions(BigDecimal.valueOf(1500.234))
                .build();
        when(aviationAerCorsiaEmissionsCalculationService.calculateTotalSubmittedEmissions(aer, Year.of(2022)))
        	.thenReturn(totalEmissions);
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
            .thenReturn(Optional.of(latestVerificationBodyDetails));
       
        //invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload resultPayload = (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AviationAerCorsiaVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(requestPayload.getVerificationData())
        		.build());
        assertEquals(serviceContactDetails, resultPayload.getServiceContactDetails());
        assertThat(resultPayload.getTotalEmissionsProvided()).isEqualTo(totalEmissions.getAllFlightsEmissions());
        assertThat(resultPayload.getTotalOffsetEmissionsProvided()).isEqualTo(totalEmissions.getOffsetFlightsEmissions());
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        
        assertThat(resultPayload.getVerificationReport().getVerificationData().getEmissionsReductionClaimVerification()).isNull();
        
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer, Year.of(2022));
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }
    

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }
}
