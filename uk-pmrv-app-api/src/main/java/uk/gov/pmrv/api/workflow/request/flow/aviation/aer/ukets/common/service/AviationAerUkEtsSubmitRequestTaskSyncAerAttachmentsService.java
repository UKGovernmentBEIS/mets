package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

@Service
public class AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService {

	public void sync(Boolean reportingRequired,
			AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload) {
		// handle aer attachments
        if(BooleanUtils.isTrue(reportingRequired) &&
        		BooleanUtils.isNotTrue(requestTaskPayload.getReportingRequired())) {
        	refreshAerAttachmentsUponReportingObligationTrue(requestTaskPayload);
        } else {
        	// do nothing
        }
	}
	
	private void refreshAerAttachmentsUponReportingObligationTrue(AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload) {
		requestTaskPayload.getAerAttachments().clear();
		// load from emp originated data
    	requestTaskPayload.getAerAttachments().putAll(requestTaskPayload.getEmpOriginatedData().getOperatorDetailsAttachments());
	}
}
