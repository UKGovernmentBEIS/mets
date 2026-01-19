package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class BDRS2SubmitService {

    public void applySaveAction(RequestTask requestTask,
                                BDRS2ApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setBdrs2SectionsCompleted(
                taskActionPayload.getBdrs2SectionsCompleted());
        taskPayload.setBdrs2(taskActionPayload.getBdrs2());
        taskPayload.setBdrs2FileVersion(taskActionPayload.getBdrs2FileVersion());
    }
}
