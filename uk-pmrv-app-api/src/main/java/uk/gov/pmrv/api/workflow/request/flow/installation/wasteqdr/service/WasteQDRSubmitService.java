package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class WasteQDRSubmitService {

    public void applySaveAction(RequestTask requestTask,
                                WasteQDRApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final WasteQDRApplicationSubmitRequestTaskPayload taskPayload =
                (WasteQDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setWasteQDRSectionsCompleted(
                taskActionPayload.getWasteQDRSectionsCompleted());
        taskPayload.setQdr(taskActionPayload.getQdr());
    }
}
