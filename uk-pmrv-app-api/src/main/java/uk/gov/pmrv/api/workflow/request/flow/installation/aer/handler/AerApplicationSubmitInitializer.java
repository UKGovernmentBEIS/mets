package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.util.Set;



@Service
@AllArgsConstructor
public class AerApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final Long accountId = request.getAccountId();
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
            .getInstallationOperatorDetails(accountId);
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        final Aer aer = requestPayload.getAer();
        final PermitOriginatedData permitOriginatedData = requestPayload.getPermitOriginatedData();

        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) request.getMetadata();

        // Get verification body id in case of verification performed
        Long verificationBodyId = !ObjectUtils.isEmpty(requestPayload.getVerificationReport()) ?
            requestPayload.getVerificationReport().getVerificationBodyId() :
            null;

        return AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .installationOperatorDetails(installationOperatorDetails)
            .permitType(permitOriginatedData.getPermitType())
            .aer(aer)
            .aerAttachments(requestPayload.getAerAttachments())
            .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
            .monitoringPlanVersions(requestPayload.getMonitoringPlanVersions())
            .permitOriginatedData(permitOriginatedData)
            .reportingYear(aerRequestMetadata.getYear())
            .verificationPerformed(requestPayload.isVerificationPerformed())
            .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
            .verificationBodyId(verificationBodyId)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AER_APPLICATION_SUBMIT);
    }

}
