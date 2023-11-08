package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreUkEtsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class RequestAviationDreUkEtsApplyService {

    private final AviationDreUkEtsValidatorService aviationDreUkEtsValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final AviationAccountQueryService aviationAccountQueryService;

    @Transactional
    public void applySaveAction(
        AviationDreUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AviationDreUkEtsApplicationSubmitRequestTaskPayload taskPayload = (AviationDreUkEtsApplicationSubmitRequestTaskPayload)requestTask.getPayload();
        taskPayload.setDre(taskActionPayload.getDre());
        taskPayload.setSectionCompleted(taskActionPayload.getSectionCompleted());
    }

    @Transactional
    public void applySubmitNotify(RequestTask requestTask, DecisionNotification decisionNotification, PmrvUser pmrvUser) {
        final AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationDreUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final AviationDre dre = requestTaskPayload.getDre();

        aviationDreUkEtsValidatorService.validateAviationDre(dre);
        if(!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) requestTask.getRequest().getPayload();
        final Long accountId = requestTask.getRequest().getAccountId();
        AviationAccountInfoDTO
            aviationAccountInfoDTO = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);
        if (aviationAccountInfoDTO.getLocation() == null) {
            throw new BusinessException(ErrorCode.AVIATION_ACCOUNT_LOCATION_NOT_EXIST);
        }
        requestPayload.setDecisionNotification(decisionNotification);
        updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, String peerReviewer) {
        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) requestTask.getRequest().getPayload();
        final AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationDreUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
    }

    private void updateRequestPayloadWithSubmitRequestTaskPayload(final AviationDreUkEtsRequestPayload requestPayload,
                                                                  final AviationDreUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload) {
        final AviationDre dre = requestTaskPayload.getDre();
        requestPayload.setDre(dre);
        requestPayload.setSectionCompleted(requestTaskPayload.getSectionCompleted());
        requestPayload.setDreAttachments(requestTaskPayload.getDreAttachments());
    }
}
