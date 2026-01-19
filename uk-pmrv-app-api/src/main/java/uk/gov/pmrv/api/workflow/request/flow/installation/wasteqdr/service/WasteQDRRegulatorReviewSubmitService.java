package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

@Service
@RequiredArgsConstructor
public class WasteQDRRegulatorReviewSubmitService {

    private final WasteQDRValidationService validationService;

    @Transactional
    public void submit( final RequestTask requestTask, final AppUser appUser) {
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReviewDecision(taskPayload);

        updateRequestPayload(requestTask, taskPayload, appUser);
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload,
                                     final AppUser appUser) {
        Request request = requestTask.getRequest();
        WasteQDRRequestPayload requestPayload = (WasteQDRRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setReviewDecision(reviewRequestTaskPayload.getReviewDecision());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());
    }

}
