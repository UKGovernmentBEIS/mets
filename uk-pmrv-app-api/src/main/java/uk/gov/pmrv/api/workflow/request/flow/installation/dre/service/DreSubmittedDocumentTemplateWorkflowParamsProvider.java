package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

import java.util.HashMap;
import java.util.Map;

@Component
class DreSubmittedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<DreRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.DRE_SUBMIT;
	}

	@Override
	public Map<String, Object> constructParams(DreRequestPayload payload, String requestId) {
		final Dre dre = payload.getDre();
		
		final Map<String, Object> vars = new HashMap<>();
		
		vars.put("initiatorRequest", payload.getInitiatorRequest());
		vars.put("reportingYear", payload.getReportingYear());
		vars.put("determinationReasonDescription",
				dre.getDeterminationReason().getType() == DreDeterminationReasonType.OTHER
						? dre.getDeterminationReason().getTypeOtherSummary()
						: dre.getDeterminationReason().getType().getDescription());
		vars.put("officialNoticeReason", dre.getOfficialNoticeReason());
		vars.put("informationSources", dre.getInformationSources());
		vars.put("chargeOperator", dre.getFee().isChargeOperator());
		if(dre.getFee().isChargeOperator()) {
			vars.put("feeAmount", dre.getFee().getFeeAmount());
			vars.put("feeDetails", dre.getFee().getFeeDetails());
		}
		
		vars.put("reportableEmissions", dre.getMonitoringApproachReportingEmissions());
		vars.put("totalReportableEmissions", dre.getTotalReportableEmissions());
		vars.put("transferredEmissions", dre.getTotalTransferredEmissions());
		
		return vars;
	}

}
