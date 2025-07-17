package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        final AviationAerUkEtsRequestPayload aerUkEtsRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        final AviationAerRequestMetadata aerUkEtsRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();

		final AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = aviationAerUkEtsReviewMapper
				.toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload(aerUkEtsRequestPayload,
						RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
						aerUkEtsRequestMetadata.getYear());
        
        // refresh service contact details
        requestTaskPayload.setServiceContactDetails(accountInfo.getServiceContactDetails());
        
        // refresh crco code
        final String latestCrcoCode = accountInfo.getCrcoCode();
        requestTaskPayload.getEmpOriginatedData().getOperatorDetails().setCrcoCode(latestCrcoCode);
        final AviationAerUkEts aer = requestTaskPayload.getAer();
        if(aer != null) {
        	requestTaskPayload.getAer().getOperatorDetails().setCrcoCode(latestCrcoCode);
        }
        
        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT);
    }
}
