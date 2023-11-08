package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceFinalDeterminationApplyService {

    public void applySaveAction(final RequestTask requestTask,
                                final NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NonComplianceFinalDeterminationRequestTaskPayload
            requestTaskPayload = (NonComplianceFinalDeterminationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setFinalDetermination(taskActionPayload.getFinalDetermination());
        requestTaskPayload.setDeterminationCompleted(taskActionPayload.getDeterminationCompleted());
    }
}
