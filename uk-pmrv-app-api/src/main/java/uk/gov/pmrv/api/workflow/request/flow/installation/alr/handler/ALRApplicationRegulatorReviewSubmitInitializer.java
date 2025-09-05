package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.service.AllowanceQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ALRApplicationRegulatorReviewSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestVerificationService requestVerificationService;
    private final AllowanceQueryService allowanceQueryService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        //If regulator review outcome is null, get historical data
        if (requestPayload.getRegulatorReviewOutcome() == null) {
            final List<HistoricalActivityLevel> historicalActivityLevels = allowanceQueryService
                    .getHistoricalActivityLevelsByAccount(accountId);
            requestPayload.setRegulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().historicalActivityLevels(historicalActivityLevels).build());
        }

        requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
                request.getVerificationBodyId());

        return ALR_MAPPER.toALRApplicationRegulatorReviewSubmitRequestTaskPayload(
                requestPayload,
                RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT);
    }
}
