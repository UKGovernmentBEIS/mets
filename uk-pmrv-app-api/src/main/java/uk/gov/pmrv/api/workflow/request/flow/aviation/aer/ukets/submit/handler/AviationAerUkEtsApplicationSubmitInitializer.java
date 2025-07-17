package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsApplicationSubmitInitializer implements InitializeRequestTaskHandler {
	
	private final AviationAccountQueryService aviationAccountQueryService;

    private final AccountContactQueryService accountContactQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        AviationAerRequestMetadata requestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        final Long verificationBodyId = !ObjectUtils.isEmpty(requestPayload.getVerificationReport()) ?
                                    requestPayload.getVerificationReport().getVerificationBodyId() :
                                    null;
        

        final AviationAccountInfoDTO aviationAccountInfoDTO = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());
        final AviationAccountReportingStatus accountReportingStatus = aviationAccountInfoDTO.getReportingStatus();
        boolean sendEmailNotification = !AviationAccountReportingStatus.EXEMPT_COMMERCIAL.equals(accountReportingStatus)
            && !AviationAccountReportingStatus.EXEMPT_NON_COMMERCIAL.equals(accountReportingStatus);

        //refresh crcocode with latest
        final String latestCrcoCode = aviationAccountInfoDTO.getCrcoCode();

        final EmpUkEtsOriginatedData originatedData = requestPayload.getEmpOriginatedData();
		originatedData.getOperatorDetails().setCrcoCode(latestCrcoCode);
		final AviationAerUkEts aer = requestPayload.getAer();
		if(aer != null) {
			aer.getOperatorDetails().setCrcoCode(latestCrcoCode);
		}
		
        return AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .sendEmailNotification(sendEmailNotification)
                .reportingYear(requestMetadata.getYear())
                .reportingRequired(requestPayload.getReportingRequired())
                .reportingObligationDetails(requestPayload.getReportingObligationDetails())
                .aer(requestPayload.getAer())
                .aerAttachments(requestPayload.getAerAttachments())
                .aerSectionsCompleted(requestPayload.getAerSectionsCompleted())
                .empOriginatedData(originatedData)
                .aerMonitoringPlanVersions(requestPayload.getAerMonitoringPlanVersions())
                .serviceContactDetails(accountContactQueryService.getServiceContactDetails(request.getAccountId()).orElse(null))
                .verificationBodyId(verificationBodyId)
                .verificationPerformed(requestPayload.isVerificationPerformed())
                .verificationSectionsCompleted(requestPayload.getVerificationSectionsCompleted())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT);
    }

}
