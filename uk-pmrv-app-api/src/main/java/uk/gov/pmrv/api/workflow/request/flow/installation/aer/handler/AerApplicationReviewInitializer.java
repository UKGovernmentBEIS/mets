package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AerApplicationReviewInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) request.getMetadata();
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(aerRequestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        return aerMapper.toAerApplicationReviewRequestTaskPayload(
        		aerRequestPayload,
                installationOperatorDetails,
                RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD,
                aerRequestMetadata.getYear()
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_REVIEW);
    }
}
