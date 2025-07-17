package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

@Service
public class AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService {

	public void sync(Boolean reportingRequired,
			AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload) {
		// handle aer attachments
        if(BooleanUtils.isTrue(reportingRequired) &&
        		BooleanUtils.isNotTrue(requestTaskPayload.getReportingRequired())) {
        	refreshAerAttachmentsUponReportingObligationTrue(requestTaskPayload);
        } else {
        	// do nothing
        }
	}
	
	private void refreshAerAttachmentsUponReportingObligationTrue(AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload) {
		requestTaskPayload.getAerAttachments().clear();
		// load from emp originated data
    	requestTaskPayload.getAerAttachments().putAll(requestTaskPayload.getEmpOriginatedData().getOperatorDetailsAttachments());
	}
}
