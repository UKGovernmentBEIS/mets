package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AerCompleteService {

    private final RequestService requestService;
    private final AerService aerService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        final AerRequestMetadata metadata = (AerRequestMetadata) request.getMetadata();
        final Long accountId = request.getAccountId();

        // Update Installation details
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        // Save AER to DB
        AerContainer aerContainer = aerMapper.toAerContainer(requestPayload, installationOperatorDetails, metadata);
        AerSubmitParams params = AerSubmitParams.builder()
                .accountId(accountId)
                .aerContainer(aerContainer)
                .build();
        BigDecimal totalEmissions = aerService.submitAer(params);

        // Save verifier decision and total emissions in request metadata
        Optional.ofNullable(aerContainer.getVerificationReport()).ifPresent(report ->
                metadata.setOverallAssessmentType(report.getVerificationData().getOverallAssessment().getType()));
        metadata.setEmissions(totalEmissions);
    }

    public void addRequestAction(final String requestId, boolean skipped) {
        final Request request = requestService.findRequestById(requestId);
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        final AerRequestMetadata metadata = (AerRequestMetadata) request.getMetadata();

        // Update Installation details
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        final AerApplicationCompletedRequestActionPayload actionPayload = aerMapper.toAerApplicationCompletedRequestActionPayload(
                requestPayload, installationOperatorDetails, requestPayload.getVerificationReport(), metadata.getYear());

        RequestActionType actionType = skipped ?
                RequestActionType.AER_APPLICATION_REVIEW_SKIPPED :
                RequestActionType.AER_APPLICATION_COMPLETED;

        if(skipped){
            actionPayload.setAerSkipReviewDecision(requestPayload.getSkipReviewDecision());
        }

        // Add action completed
        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }
}
