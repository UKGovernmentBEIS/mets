package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.handler;

import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AviationAccountQueryService aviationAccountQueryService;

    private final AccountContactQueryService accountContactQueryService;
    
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAerCorsiaRequestMetadata requestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        final AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        final Long verificationBodyId = !ObjectUtils.isEmpty(requestPayload.getVerificationReport()) ?
                                    requestPayload.getVerificationReport().getVerificationBodyId() :
                                    null;

        final AviationAccountInfoDTO aviationAccountInfoDTO = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());
        final AviationAccountReportingStatus accountReportingStatus = aviationAccountInfoDTO.getReportingStatus();
        boolean sendEmailNotification = !AviationAccountReportingStatus.EXEMPT_COMMERCIAL.equals(accountReportingStatus)
            && !AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL.equals(accountReportingStatus);

        return AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .sendEmailNotification(sendEmailNotification)
                .reportingYear(requestMetadata.getYear())
                .reportingRequired(requestPayload.getReportingRequired())
                .reportingObligationDetails(requestPayload.getReportingObligationDetails())
                .aer(requestPayload.getAer())
                .aerAttachments(requestPayload.getAerAttachments())
                .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
                .empOriginatedData(requestPayload.getEmpOriginatedData())
                .aerMonitoringPlanVersions(requestPayload.getAerMonitoringPlanVersions())
                .serviceContactDetails(accountContactQueryService.getServiceContactDetails(request.getAccountId()).orElse(null))
                .verificationBodyId(verificationBodyId)
                .verificationPerformed(requestPayload.isVerificationPerformed())
                .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT);
    }

}
