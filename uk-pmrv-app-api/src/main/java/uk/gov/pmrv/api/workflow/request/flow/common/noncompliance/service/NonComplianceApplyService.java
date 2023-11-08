package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestTaskClosable;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseJustification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceApplyService {

    public void applyCloseAction(final RequestTask requestTask,
                                 final NonComplianceCloseApplicationRequestTaskActionPayload taskActionPayload) {

        final NonComplianceCloseJustification closeJustification = taskActionPayload.getCloseJustification();
        
        final NonComplianceRequestTaskClosable
            requestTaskPayload = (NonComplianceRequestTaskClosable) requestTask.getPayload();
        requestTaskPayload.setCloseJustification(closeJustification);
        
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setCloseJustification(closeJustification);
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
    }

    public void applySaveAction(final RequestTask requestTask,
                                final NonComplianceSaveApplicationRequestTaskActionPayload taskActionPayload) {
        
        final NonComplianceApplicationSubmitRequestTaskPayload
            requestTaskPayload = (NonComplianceApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        
        requestTaskPayload.setReason(taskActionPayload.getReason());
        requestTaskPayload.setNonComplianceDate(taskActionPayload.getNonComplianceDate());
        requestTaskPayload.setComplianceDate(taskActionPayload.getComplianceDate());
        requestTaskPayload.setComments(taskActionPayload.getComments());
        requestTaskPayload.setSelectedRequests(taskActionPayload.getSelectedRequests());
        requestTaskPayload.setNonCompliancePenalties(taskActionPayload.getNonCompliancePenalties());
        requestTaskPayload.setSectionCompleted(taskActionPayload.getSectionCompleted());
    }
}
