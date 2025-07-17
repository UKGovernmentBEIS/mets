package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;

import java.time.Year;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final AviationAerCorsiaAnnualOffsettingRequestMetadata requestMetadata = (AviationAerCorsiaAnnualOffsettingRequestMetadata) request.getMetadata();
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();

        Year year = requestMetadata.getYear();

        AviationAerCorsiaAnnualOffsetting offsetting = requestPayload.getAviationAerCorsiaAnnualOffsetting();

        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = AviationAerCorsiaAnnualOffsetting.builder()
            .schemeYear(year)
            .sectorGrowth(Optional.ofNullable(offsetting).map(AviationAerCorsiaAnnualOffsetting::getSectorGrowth).orElse(null))
            .totalChapter(Optional.ofNullable(offsetting).map(AviationAerCorsiaAnnualOffsetting::getTotalChapter).orElse(null))
            .calculatedAnnualOffsetting(Optional.ofNullable(offsetting).map(AviationAerCorsiaAnnualOffsetting::getCalculatedAnnualOffsetting).orElse(null))
            .build();

        return AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD)
                .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(requestPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted())
                .decisionNotification(requestPayload.getDecisionNotification())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT);
    }
}
