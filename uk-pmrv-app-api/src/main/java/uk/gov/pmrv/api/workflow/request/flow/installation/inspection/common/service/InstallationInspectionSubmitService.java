package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSaveRequestTaskActionPayload;


public interface InstallationInspectionSubmitService {

    void applySaveAction(final RequestTask requestTask,
                         final InstallationInspectionApplicationSaveRequestTaskActionPayload taskActionPayload);

    void cancel(final String requestId);

    void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser);

    RequestType getRequestType();

    void applySubmitNotify(RequestTask requestTask, DecisionNotification decisionNotification, AppUser appUser);
}
