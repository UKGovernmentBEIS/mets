package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermitVariationReviewMapper PERMIT_VARIATION_MAPPER = Mappers.getMapper(PermitVariationReviewMapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    
    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            PERMIT_VARIATION_MAPPER.toPermitVariationApplicationReviewRequestTaskPayload(
                requestPayload, RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD
            );

        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
            .getInstallationOperatorDetails(request.getAccountId());
        taskPayload.setInstallationOperatorDetails(installationOperatorDetails);
        
        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW);
    }
}
