package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveAuthorityResponseRequestTaskActionPayload;

@Service
public class NerAuthorityResponseService {

    @Transactional
    public void applyAuthorityResponseSaveAction(final RequestTask requestTask,
                                                 final NerSaveAuthorityResponseRequestTaskActionPayload actionPayload) {

        final NerAuthorityResponseRequestTaskPayload taskPayload =
            (NerAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        taskPayload.setSubmittedToAuthorityDate(actionPayload.getSubmittedToAuthorityDate());
        taskPayload.setAuthorityResponse(actionPayload.getAuthorityResponse());
        taskPayload.setNerSectionsCompleted(actionPayload.getNerSectionsCompleted());
    }
    
    @Transactional
    public void saveAuthorityDecisionNotification(final RequestTask requestTask,
                                                  final DecisionNotification decisionNotification,
                                                  final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final NerAuthorityResponseRequestTaskPayload taskPayload =
            (NerAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        final NerRequestPayload requestPayload =
            (NerRequestPayload) request.getPayload();

        requestPayload.setSubmittedToAuthorityDate(taskPayload.getSubmittedToAuthorityDate());
        requestPayload.setAuthorityResponse(taskPayload.getAuthorityResponse());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setDecisionNotification(decisionNotification);
    }
}
