package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

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

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    private static final PermitVariationReviewMapper permitReviewMapper = Mappers.getMapper(PermitVariationReviewMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());
        PermitVariationApplicationReviewRequestTaskPayload permitVariationApplicationReviewRequestTaskPayload =
            permitReviewMapper.toPermitVariationApplicationReviewRequestTaskPayload((PermitVariationRequestPayload) request.getPayload(),
                RequestTaskPayloadType.PERMIT_VARIATION_WAIT_FOR_AMENDS_PAYLOAD);
        permitVariationApplicationReviewRequestTaskPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return permitVariationApplicationReviewRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_VARIATION_WAIT_FOR_AMENDS);
    }
}
