package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.mapper.AviationAerCorsia3YearPeriodOffsettingMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingCalculationsService;

@Service
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingApplicationSubmitInitializer
        implements InitializeRequestTaskHandler {

    private final RequestService requestService;
    private final AviationAerRequestQueryService aviationAerRequestQueryService;
    private final AviationAerRequestIdGenerator aviationAerRequestIdGenerator;
    private final AviationAerCorsiaAnnualOffsettingRequestQueryService annualOffsettingRequestQueryService;
    private final AviationAerCorsia3YearPeriodOffsettingCalculationsService calculationsService;
    private static final AviationAerCorsia3YearPeriodOffsettingMapper MAPPER =
            Mappers.getMapper(AviationAerCorsia3YearPeriodOffsettingMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) request.getPayload();

        if (aviationAerCorsiaThreeYearPeriodOffsettingExists(requestPayload)) {
            return MAPPER.toAviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload(
                    requestPayload,
                    RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD);
        }

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata =
                (AviationAerCorsia3YearPeriodOffsettingRequestMetadata) request.getMetadata();

        List<Year> schemeYears = List.of(requestMetadata.getYear().minusYears(2),
                requestMetadata.getYear().minusYears(1),
                requestMetadata.getYear());

        Request aerRequest = requestService.findRequestById(requestMetadata.getCurrentAerId());

        Map<Year, Optional<Request>> aerRequests = getAerRequests(aerRequest, requestMetadata, schemeYears);

        Map<Year, Optional<Request>> annualOffsettingRequests =
                getAnnualOffsettingRequests(aerRequest, requestMetadata, schemeYears);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData =
                getYearlyOffsettingData(aerRequests, annualOffsettingRequests, schemeYears);

        AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData =
                getTotalYearlyOffsettingData(yearlyOffsettingData);

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                        .builder()
                        .payloadType(RequestTaskPayloadType
                                        .AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD)
                        .aviationAerCorsia3YearPeriodOffsetting(AviationAerCorsia3YearPeriodOffsetting
                                .builder()
                                .schemeYears(schemeYears)
                                .yearlyOffsettingData(yearlyOffsettingData)
                                .totalYearlyOffsettingData(totalYearlyOffsettingData)
                                .periodOffsettingRequirements(calculationsService
                                        .calculatePeriodOffsettingRequirements(
                                                totalYearlyOffsettingData.getCalculatedAnnualOffsetting(),
                                                totalYearlyOffsettingData.getCefEmissionsReductions()))
                                .build())
                        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(
                                requestPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted())
                        .decisionNotification(requestPayload.getDecisionNotification())
                        .build();


        return requestTaskPayload;
    }

    private  Map<Year, Optional<Request>> getAerRequests(Request initialAerRequest,
                                                         AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata,
                                                         List<Year> schemeYears) {

        Optional<Request> aerRequestYearBefore =
                aviationAerRequestQueryService.findAviationAerById(
                        aviationAerRequestIdGenerator.generatePastAerId(initialAerRequest.getAccountId(),
                                requestMetadata.getYear(),1));

        Optional<Request> aerRequestTwoYearsBefore =
                aviationAerRequestQueryService.findAviationAerById(
                        aviationAerRequestIdGenerator.generatePastAerId(initialAerRequest.getAccountId(),
                                requestMetadata.getYear(),2));

        return Map.of(schemeYears.get(0), aerRequestTwoYearsBefore,
                schemeYears.get(1), aerRequestYearBefore,
                schemeYears.get(2), Optional.of(initialAerRequest));
    }

    private Map<Year, Optional<Request>> getAnnualOffsettingRequests(Request initialAerRequest,
                                                         AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata,
                                                         List<Year> schemeYears) {

        Optional<Request> annualOffsetting = annualOffsettingRequestQueryService
                .findLatestCorsiaAnnualOffsettingRequestForYear(
                        initialAerRequest.getAccountId(), requestMetadata.getYear());

        Optional<Request> annualOffsettingYearBefore = annualOffsettingRequestQueryService
                .findLatestCorsiaAnnualOffsettingRequestForYear(initialAerRequest.getAccountId(),
                        requestMetadata.getYear().minusYears(1));

        Optional<Request> annualOffsettingTwoYearsBefore = annualOffsettingRequestQueryService
                .findLatestCorsiaAnnualOffsettingRequestForYear(initialAerRequest.getAccountId(),
                        requestMetadata.getYear().minusYears(2));

        return Map.of(schemeYears.get(0), annualOffsettingTwoYearsBefore,
                schemeYears.get(1), annualOffsettingYearBefore,
                schemeYears.get(2), annualOffsetting);
    }

    private Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> getYearlyOffsettingData(Map<Year, Optional<Request>> aerRequests,
                                                                                                          Map<Year, Optional<Request>> annualOffsettingRequests,
                                                                                                          List<Year> schemeYears) {

        return Map.of(
                schemeYears.get(0),
                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(
                                getCalculatedAnnualOffsettingValueFromAnnualOffsettingRequest(annualOffsettingRequests.get(schemeYears.getFirst())))
                        .cefEmissionsReductions(
                                 getCefEmissionsReductionsValueFromAerRequest(aerRequests.get(schemeYears.getFirst())))
                        .build(),
                schemeYears.get(1),
                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(
                                getCalculatedAnnualOffsettingValueFromAnnualOffsettingRequest(annualOffsettingRequests.get(schemeYears.get(1))))
                        .cefEmissionsReductions(
                                 getCefEmissionsReductionsValueFromAerRequest(aerRequests.get(schemeYears.get(1))))
                        .build(),
                schemeYears.get(2),
                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(
                                getCalculatedAnnualOffsettingValueFromAnnualOffsettingRequest(annualOffsettingRequests.get(schemeYears.get(2))))
                        .cefEmissionsReductions(
                                 getCefEmissionsReductionsValueFromAerRequest(aerRequests.get(schemeYears.get(2))))
                        .build()
        );

    }

    private AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData getTotalYearlyOffsettingData(
            Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData) {

        Long totalCalculatedAnnualOffsetting = calculationsService
                .calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(yearlyOffsettingData);

        Long totalCefEmissionsReductions = calculationsService
                .calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(yearlyOffsettingData);

        return AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .cefEmissionsReductions(totalCefEmissionsReductions)
                .calculatedAnnualOffsetting(totalCalculatedAnnualOffsetting)
                .build();
    }


    private Long getCefEmissionsReductionsValueFromAerRequest(Optional<Request> aerRequest) {

        if (aerRequest.isPresent()) {
            AviationAerCorsiaRequestPayload requestPayload =
                    (AviationAerCorsiaRequestPayload) aerRequest.get().getPayload();
            if (!ObjectUtils.isEmpty(requestPayload.getSubmittedEmissions())) {
                if (!ObjectUtils.isEmpty(requestPayload.getSubmittedEmissions().getTotalEmissions())) {
                    return requestPayload
                            .getSubmittedEmissions()
                            .getTotalEmissions()
                            .getEmissionsReductionClaim()
                            .longValue();
                }
            }
        }

        return null;
    }

    private Long getCalculatedAnnualOffsettingValueFromAnnualOffsettingRequest(Optional<Request>
                                                                                          annualOffsettingRequest) {

        if (annualOffsettingRequest.isPresent()) {
            AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                    (AviationAerCorsiaAnnualOffsettingRequestPayload) annualOffsettingRequest.get().getPayload();

            if (!ObjectUtils.isEmpty(requestPayload.getAviationAerCorsiaAnnualOffsetting())) {
                return requestPayload
                        .getAviationAerCorsiaAnnualOffsetting()
                        .getCalculatedAnnualOffsetting().longValue();
            }
        }

        return null;
    }

    private boolean aviationAerCorsiaThreeYearPeriodOffsettingExists(AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload) {
        return !ObjectUtils.isEmpty(requestPayload.getAviationAerCorsia3YearPeriodOffsetting());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT);
    }
}
