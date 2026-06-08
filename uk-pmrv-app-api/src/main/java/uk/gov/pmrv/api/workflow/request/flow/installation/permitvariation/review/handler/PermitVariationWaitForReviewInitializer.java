package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationWaitForReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationWaitForReviewInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final PermitVariationReviewMapper PERMIT_VARIATION_REVIEW_MAPPER = Mappers.getMapper(PermitVariationReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final PermitVariationWaitForReviewRequestTaskPayload requestTaskPayload = PERMIT_VARIATION_REVIEW_MAPPER.toPermitVariationWaitForReviewRequestTaskPayload(
                requestPayload, RequestTaskPayloadType.PERMIT_VARIATION_WAIT_FOR_REVIEW_PAYLOAD);

        requestTaskPayload.setInstallationOperatorDetails(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()));

        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_VARIATION_WAIT_FOR_REVIEW);
    }

}
