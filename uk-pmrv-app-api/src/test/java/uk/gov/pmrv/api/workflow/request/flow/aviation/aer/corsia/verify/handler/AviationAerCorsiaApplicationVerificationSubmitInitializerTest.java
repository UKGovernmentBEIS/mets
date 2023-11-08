package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerMonitoringPlanChanges;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplicationVerificationSubmitInitializerTest {

    @InjectMocks
    private AviationAerCorsiaApplicationVerificationSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private RequestVerificationService<AviationAerCorsiaVerificationReport> requestVerificationService;

    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    @Test
    void initializePayload() {
        Long vbId = 1L;
        Long accountId = 2L;
        Year reportingYear = Year.of(2022);
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder().exist(false).build())
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("name")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("icao designators")
                    .build())
                .build())
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
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
            .aerMonitoringPlanChanges(AviationAerMonitoringPlanChanges.builder().notCoveredChangesExist(true).details("not covered changes details").build())
            .build();
        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP-ID").empApprovalDate(LocalDate.of(2022, 2, 2)).build()
        );
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attach1"
        );
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
            .reportingRequired(true)
            .aer(aer)
            .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
            .aerAttachments(aerAttachments)
            .build();
        AviationAerCorsiaRequestMetadata aerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build();

        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
            .payload(requestPayload)
            .verificationBodyId(vbId)
            .accountId(accountId)
            .metadata(aerRequestMetadata)
            .build();

        String accountOperatorName = "operatorName";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("service_contact_name").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .operatorName(accountOperatorName)
            .build();

        VerificationBodyDetails verificationBodyDetails = VerificationBodyDetails.builder()
            .name("vb_name")
            .accreditationReferenceNumber("accr_ref_number")
            .emissionTradingSchemes(Set.of(EmissionTradingScheme.CORSIA))
            .build();

        AviationAerCorsiaTotalEmissions aviationAerCorsiaTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
            .allFlightsEmissions(BigDecimal.valueOf(12588.45))
            .offsetFlightsEmissions(BigDecimal.valueOf(1500.234))
            .build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        doNothing().when(requestVerificationService).clearVerificationReport(requestPayload, vbId);
        when(requestVerificationService.getVerificationBodyDetails(requestPayload.getVerificationReport(), vbId))
            .thenReturn(verificationBodyDetails);
        when(aviationAerCorsiaEmissionsCalculationService.calculateTotalSubmittedEmissions(aer))
            .thenReturn(aviationAerCorsiaTotalEmissions);

        RequestTaskPayload result = initializer.initializePayload(request);

        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload resultRequestTaskPayload = (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) result;

        AviationAerCorsiaVerificationReport verificationReport = resultRequestTaskPayload.getVerificationReport();
        assertEquals(vbId, verificationReport.getVerificationBodyId());
        assertEquals(verificationBodyDetails, verificationReport.getVerificationBodyDetails());
        assertEquals(Set.of(AviationAerCorsiaFuelType.JET_KEROSENE),
            verificationReport.getVerificationData().getOpinionStatement().getFuelTypes());


        AviationAerCorsia resultAer = resultRequestTaskPayload.getAer();

        AviationCorsiaOperatorDetails resultAerOperatorDetails = resultAer.getOperatorDetails();

        assertEquals(aer.getOperatorDetails().getOperatorName(), resultAerOperatorDetails.getOperatorName());

        assertEquals(reportingYear, resultRequestTaskPayload.getReportingYear());
        assertEquals(serviceContactDetails, resultRequestTaskPayload.getServiceContactDetails());
        assertThat(resultRequestTaskPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(resultRequestTaskPayload.getAerMonitoringPlanVersions()).containsExactlyInAnyOrderElementsOf(aerMonitoringPlanVersions);
        assertEquals(aviationAerCorsiaTotalEmissions.getAllFlightsEmissions(), resultRequestTaskPayload.getTotalEmissionsProvided());
        assertEquals(aviationAerCorsiaTotalEmissions.getOffsetFlightsEmissions(), resultRequestTaskPayload.getTotalOffsetEmissionsProvided());

        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestVerificationService, times(1)).clearVerificationReport(requestPayload, vbId);
        verify(requestVerificationService, times(1))
            .getVerificationBodyDetails(requestPayload.getVerificationReport(), vbId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(
            RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }
}
