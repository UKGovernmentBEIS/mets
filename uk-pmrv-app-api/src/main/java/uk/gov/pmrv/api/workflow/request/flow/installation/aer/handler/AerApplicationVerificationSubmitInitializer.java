package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationReportSectionInitializationService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT;

@Service
@RequiredArgsConstructor
public class AerApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService<AerVerificationReport> requestVerificationService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    private final List<AerVerificationReportSectionInitializationService> aerVerificationReportSectionInitializationServices;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) request.getMetadata();

        // If VB id is changed clear verification report from request
        requestVerificationService.clearVerificationReport(aerRequestPayload, request.getVerificationBodyId());

        // Set Verification Body details
        AerVerificationReport verificationReport = Optional.ofNullable(aerRequestPayload.getVerificationReport())
                .orElse(AerVerificationReport.builder()
                        .verificationData(initializeAerVerificationReport(aerRequestPayload.getAer()))
                        .build());
        verificationReport.setVerificationBodyDetails(requestVerificationService
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()));

        return aerMapper.toAerApplicationVerificationRequestTaskPayload(
                (AerRequestPayload) request.getPayload(),
                installationOperatorDetails,
                verificationReport,
                aerRequestMetadata.getYear()
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT, AER_AMEND_APPLICATION_VERIFICATION_SUBMIT);
    }

    private AerVerificationData initializeAerVerificationReport(Aer aer) {
        final AerVerificationData aerVerificationData = AerVerificationData.builder()
                .opinionStatement(new OpinionStatement())
                .build();
        aerVerificationReportSectionInitializationServices.forEach(sectionInitializationService ->
                sectionInitializationService.initialize(aerVerificationData, aer));
        return aerVerificationData;
    }
}
