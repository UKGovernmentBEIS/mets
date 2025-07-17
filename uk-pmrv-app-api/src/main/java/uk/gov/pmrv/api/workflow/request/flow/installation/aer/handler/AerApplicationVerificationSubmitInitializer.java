package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationReportSectionInitializationService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT;

@Service
@RequiredArgsConstructor
public class AerApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    private final List<AerVerificationReportSectionInitializationService> aerVerificationReportSectionInitializationServices;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        final AerRequestMetadata requestMetadata = (AerRequestMetadata) request.getMetadata();
        
        final Long requestVBId = request.getVerificationBodyId();
		final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
				.map(VerificationReport::getVerificationBodyId).orElse(null);

		// If VB id is changed clear verification report from request
		if(isVbChanged(requestVBId, verificationReportVBId)) {
			requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
            requestPayload.setVerificationAttachments(new HashMap<>());
		}

		final AerVerificationReport latestVerificationReport = AerVerificationReport.builder()
				.verificationBodyId(requestVBId)
				.verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> {
        			throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId);
        		}))
				.verificationData(isVbChanged(requestVBId, verificationReportVBId)
						? initializeAerVerificationData(requestPayload.getAer())
						: requestPayload.getVerificationData())
				.build();

        return aerMapper.toAerApplicationVerificationRequestTaskPayload(
                (AerRequestPayload) request.getPayload(),
                installationOperatorDetails,
                latestVerificationReport,
                requestMetadata.getYear()
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT, AER_AMEND_APPLICATION_VERIFICATION_SUBMIT);
    }

    private AerVerificationData initializeAerVerificationData(Aer aer) {
        final AerVerificationData aerVerificationData = AerVerificationData.builder()
                .opinionStatement(new OpinionStatement())
                .build();
        aerVerificationReportSectionInitializationServices.forEach(sectionInitializationService ->
                sectionInitializationService.initialize(aerVerificationData, aer));
        return aerVerificationData;
    }
    
    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
    	return !requestVBId.equals(verificationReportVBId);
    }
}
