package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmitParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.mapper.AviationAerUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsCompleteService {

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService requestVerificationService;
    private final AviationAerService aviationAerService;
    private final AviationAerUkEtsMapper aviationAerMapper;

    @Transactional
    public void complete(String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationAerUkEtsRequestPayload aerRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        AviationAerRequestMetadata aerMetadata = (AviationAerRequestMetadata) request.getMetadata();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(aerRequestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        AviationAerUkEtsContainer aerContainer =
            aviationAerMapper.toAviationAerUkEtsContainer(aerRequestPayload, EmissionTradingScheme.UK_ETS_AVIATION, accountInfo, aerMetadata);

        //submit aer
        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();

        Optional<AviationAerTotalReportableEmissions> reportableEmissions = aviationAerService.submitAer(submitAerParams);

        //update metadata with reportable emissions
        reportableEmissions.ifPresent(emissions -> aerMetadata.setEmissions(emissions.getReportableEmissions()));
    }

    public void addRequestAction(String requestId, boolean skipped) {
        
        Request request = requestService.findRequestById(requestId);
        AviationAerUkEtsRequestPayload aerRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        AviationAerRequestMetadata aerMetadata = (AviationAerRequestMetadata) request.getMetadata();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(aerRequestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        AviationAerUkEtsApplicationCompletedRequestActionPayload requestActionPayload =
            aviationAerMapper.toAviationAerUkEtsApplicationCompletedRequestActionPayload(
                aerRequestPayload, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD, accountInfo, aerMetadata);

        RequestActionType actionType = skipped ? 
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED :
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_COMPLETED;
        // Add action completed
        requestService.addActionToRequest(request,
            requestActionPayload,
            actionType,
            aerRequestPayload.getRegulatorReviewer());
    }
}
