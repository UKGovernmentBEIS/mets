package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerMonitoringPlanChanges;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationVerificationSubmitInitializerTest {
    
    @InjectMocks
    private AviationAerUkEtsApplicationVerificationSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;
    
    @Mock
    private RequestVerificationService<AviationAerUkEtsVerificationReport> requestVerificationService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    @Test
    void initializePayload() {
        Long vbId = 1L;
        Long accountId = 2L;
        Year reportingYear = Year.of(2022);
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("name")
                .crcoCode("code")
                .operatingLicense(OperatingLicense.builder().licenseExist(false).build())
                .build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
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
            .aerMonitoringPlanChanges(AviationAerMonitoringPlanChanges.builder().notCoveredChangesExist(true).details("not covered changes details").build())
            .build();
        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP-ID").empApprovalDate(LocalDate.of(2022, 2, 2)).build()
        );
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attach1"
        );
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
            .aerAttachments(aerAttachments)
            .build();
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(requestPayload)
            .verificationBodyId(vbId)
            .accountId(accountId)
            .metadata(aerRequestMetadata)
            .build();

        String accountOperatorName = "operatorName";
        String crcoCode = "crcoCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("service_contact_name").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .operatorName(accountOperatorName)
            .crcoCode(crcoCode)
            .build();

        VerificationBodyDetails verificationBodyDetails = VerificationBodyDetails.builder()
            .name("vb_name")
            .accreditationReferenceNumber("accr_ref_number")
            .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
            .build();

        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(12588.45);
        
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        doNothing().when(requestVerificationService).clearVerificationReport(requestPayload, vbId);
        when(requestVerificationService.getVerificationBodyDetails(requestPayload.getVerificationReport(), vbId))
            .thenReturn(verificationBodyDetails);
        when(aviationAerUkEtsEmissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(totalEmissionsProvided);

        RequestTaskPayload result = initializer.initializePayload(request);

        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload resultRequestTaskPayload = (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) result;

        AviationAerUkEtsVerificationReport verificationReport = resultRequestTaskPayload.getVerificationReport();
        assertEquals(vbId, verificationReport.getVerificationBodyId());
        assertEquals(verificationBodyDetails, verificationReport.getVerificationBodyDetails());

        AviationAerUkEtsVerificationReport resultVerificationReport = resultRequestTaskPayload.getVerificationReport();
        assertFalse(resultVerificationReport.getSafExists());
        AviationAerOpinionStatement resultVerificationReportOpinionStatement = resultVerificationReport.getVerificationData().getOpinionStatement();

        assertThat(resultVerificationReportOpinionStatement.getFuelTypes()).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE);

        AviationAerUkEts resultAer = resultRequestTaskPayload.getAer();

        AviationOperatorDetails resultAerOperatorDetails = resultAer.getOperatorDetails();

        assertEquals(crcoCode, resultAerOperatorDetails.getCrcoCode());
        assertEquals(aer.getOperatorDetails().getOperatorName(), resultAerOperatorDetails.getOperatorName());
        assertEquals(aer.getOperatorDetails().getOperatingLicense(), resultAerOperatorDetails.getOperatingLicense());

        assertEquals(reportingYear, resultRequestTaskPayload.getReportingYear());
        assertEquals(serviceContactDetails, resultRequestTaskPayload.getServiceContactDetails());
        assertThat(resultRequestTaskPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(resultRequestTaskPayload.getAerMonitoringPlanVersions()).containsExactlyInAnyOrderElementsOf(aerMonitoringPlanVersions);
        assertEquals(totalEmissionsProvided, resultRequestTaskPayload.getTotalEmissionsProvided());
        assertEquals(aer.getAerMonitoringPlanChanges().getDetails(), resultRequestTaskPayload.getNotCoveredChangesProvided());

        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestVerificationService, times(1)).clearVerificationReport(requestPayload, vbId);
        verify(requestVerificationService, times(1))
            .getVerificationBodyDetails(requestPayload.getVerificationReport(), vbId);
        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateTotalSubmittedEmissions(aer);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }
}