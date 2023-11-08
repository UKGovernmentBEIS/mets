package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class AviationAerCorsiaReviewInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService<AviationAerCorsiaVerificationReport> requestVerificationService;
    private final AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    protected abstract RequestTaskPayloadType getRequestTaskPayloadType();

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaRequestPayload aerCorsiaRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        AviationAerCorsiaRequestMetadata aerCorsiaRequestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();

        // Set Verification Body details if verification is performed
        AviationAerCorsiaVerificationReport verificationReport = aerCorsiaRequestPayload.getVerificationReport();
        Optional.ofNullable(verificationReport).ifPresent(report ->
            verificationReport.setVerificationBodyDetails(requestVerificationService
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
        );

        return aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationReviewRequestTaskPayload(
            aerCorsiaRequestPayload,
            aviationAccountInfo,
            getRequestTaskPayloadType(),
            aerCorsiaRequestMetadata.getYear()
        );
    }
}
