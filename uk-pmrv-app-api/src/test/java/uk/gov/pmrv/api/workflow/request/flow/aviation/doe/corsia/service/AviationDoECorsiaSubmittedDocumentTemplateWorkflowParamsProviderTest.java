package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Mock
    private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Mock
    private RequestService requestService;



    @Test
    void constructParams_requestPayload_all_emissions_corrected_pass_corrected_emissions() {

        Long accountId = 1L;
        String requestId = "req";

        Year reportingYear = Year.of(2022);
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_DOE_CORSIA_REQUEST_PAYLOAD)
                .reportingYear(reportingYear)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN)
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .id(requestId)
                .payload(requestPayload)
                .build();

        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.TWO)
                .reportableOffsetEmissions(BigDecimal.ONE)
                .reportableReductionClaimEmissions(BigDecimal.TEN)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(requestPayload, requestId);

        AviationDoECorsia doe = requestPayload.getDoe();
        AviationDoECorsiaEmissions emissions = doe.getEmissions();


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reportingYear", reportingYear,
                "InternationalEmissions", emissions.getEmissionsAllInternationalFlights(),
                "offsettingEmissions", emissions.getEmissionsFlightsWithOffsettingRequirements(),
                "CEFEmissions", emissions.getEmissionsClaimFromCorsiaEligibleFuels(),
                "determinationReason", doe.getDeterminationReason().getType().toString(),
                "calculationApproach", emissions.getCalculationApproach()
        ));

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_requestPayload_some_emissions_corrected_pass_all_emissions() {

        Long accountId = 1L;
        String requestId = "req";

        Year reportingYear = Year.of(2022);
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_DOE_CORSIA_REQUEST_PAYLOAD)
                .reportingYear(reportingYear)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .id(requestId)
                .payload(requestPayload)
                .build();

        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.TWO)
                .reportableOffsetEmissions(BigDecimal.ONE)
                .reportableReductionClaimEmissions(BigDecimal.TEN)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(requestPayload, requestId);

        AviationDoECorsia doe = requestPayload.getDoe();
        AviationDoECorsiaEmissions emissions = doe.getEmissions();


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reportingYear", reportingYear,
                "InternationalEmissions", emissions.getEmissionsAllInternationalFlights(),
                "offsettingEmissions", emissions.getEmissionsFlightsWithOffsettingRequirements(),
                "CEFEmissions", reportableEmissions.getReportableReductionClaimEmissions(),
                "determinationReason", doe.getDeterminationReason().getType().toString(),
                "calculationApproach", emissions.getCalculationApproach()
        ));

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_requestPayload_reportable_emissions_not_found_throw_business_exception() {

        Long accountId = 1L;
        String requestId = "req";

        Year reportingYear = Year.of(2022);
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_DOE_CORSIA_REQUEST_PAYLOAD)
                .reportingYear(reportingYear)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.empty());


        BusinessException ex =  assertThrows(BusinessException.class, () -> paramsProvider.constructParams(requestPayload, requestId));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_task_payload_all_emissions_corrected_reportable_emissions_all_present_pass_corrected_emissions() {

        Long accountId = 1L;

        Year reportingYear = Year.of(2022);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN)
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.valueOf(3))
                .reportableOffsetEmissions(BigDecimal.valueOf(2))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(11))
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(taskPayload, reportingYear, accountId);


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reportingYear", reportingYear,
                "InternationalEmissions", BigDecimal.valueOf(2),
                "offsettingEmissions", BigDecimal.valueOf(1),
                "CEFEmissions", BigDecimal.valueOf(10),
                "determinationReason", "VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED",
                "calculationApproach", "calc"
        ));

        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_task_payload_all_emissions_corrected_reportable_emissions_some_present_pass_corrected_emissions() {

        Long accountId = 1L;

        Year reportingYear = Year.of(2022);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN)
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.valueOf(3))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(11))
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(taskPayload, reportingYear, accountId);


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reportingYear", reportingYear,
                "InternationalEmissions", BigDecimal.valueOf(2),
                "offsettingEmissions", BigDecimal.valueOf(1),
                "CEFEmissions", BigDecimal.valueOf(10),
                "determinationReason", "VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED",
                "calculationApproach", "calc"
        ));

        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_task_payload_some_emissions_corrected_reportable_emissions_some_present_pass_corrected_emissions_and_saved_emissions() {

        Long accountId = 1L;

        Year reportingYear = Year.of(2022);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.valueOf(3))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(11))
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(taskPayload, reportingYear, accountId);


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reportingYear", reportingYear,
                "InternationalEmissions", BigDecimal.valueOf(2),
                "offsettingEmissions", BigDecimal.valueOf(1),
                "CEFEmissions", BigDecimal.valueOf(11),
                "determinationReason", "CORRECTIONS_TO_A_VERIFIED_REPORT",
                "calculationApproach", "calc"
        ));

        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_task_payload_emission_missing_both_from_doe_and_saved_emissions_pass_null() {

        Long accountId = 1L;

        Year reportingYear = Year.of(2022);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();


        AviationReportableEmissionsEntity reportableEmissions = AviationReportableEmissionsEntity
                .builder()
                .reportableEmissions(BigDecimal.valueOf(3))
                .reportableOffsetEmissions(BigDecimal.valueOf(11))
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.of(reportableEmissions));

        Map<String, Object> resultMap = paramsProvider.constructParams(taskPayload, reportingYear, accountId);


        Map<String, Object> vars = new HashMap<>();

        vars.put("reportingYear", reportingYear);
        vars.put("InternationalEmissions",  BigDecimal.valueOf(2));
        vars.put("offsettingEmissions",  BigDecimal.valueOf(1));
        vars.put("CEFEmissions", null);
        vars.put("determinationReason", "VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED");
        vars.put("calculationApproach", "calc");


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(vars);

        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void constructParams_task_payload_some_emissions_corrected_reportable_emissions_not_present_use_corrected_emissions() {

        Long accountId = 1L;

        Year reportingYear = Year.of(2022);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .doe(AviationDoECorsia.builder()
                    .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .calculationApproach("calc").build())
                    .fee(AviationDoECorsiaFee.builder().build())
                    .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(
                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .furtherDetails("furtherDetails")
                        .build())
                .build())
                .build();



        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear)).thenReturn(Optional.empty());

        Map<String, Object> resultMap = paramsProvider.constructParams(taskPayload, reportingYear, accountId);


        Map<String, Object> vars = new HashMap<>();

        vars.put("reportingYear", reportingYear);
        vars.put("InternationalEmissions",  BigDecimal.valueOf(2));
        vars.put("offsettingEmissions",  BigDecimal.valueOf(1));
        vars.put("CEFEmissions", null);
        vars.put("determinationReason", "CORRECTIONS_TO_A_VERIFIED_REPORT");
        vars.put("calculationApproach", "calc");


        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(vars);

        verify(aviationReportableEmissionsRepository, times(1)).findByAccountIdAndYear(accountId, reportingYear);
    }

    @Test
    void getContextActionType() {
        assertEquals(DocumentTemplateGenerationContextActionType.AVIATION_DOE_SUBMIT,paramsProvider.getContextActionType());
    }
}
