package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationSubmitRegulatorLedService {

	@Transactional
	public void savePermitVariation(
			PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask
				.getPayload();
		
		// reset determination
		taskPayload.setDetermination(null);
		
		taskPayload.setPermitType(taskActionPayload.getPermitType());
		taskPayload.setPermitVariationDetails(taskActionPayload.getPermitVariationDetails());
		taskPayload.setPermitVariationDetailsCompleted(taskActionPayload.getPermitVariationDetailsCompleted());
		taskPayload.setPermit(taskActionPayload.getPermit());
		taskPayload.setPermitSectionsCompleted(taskActionPayload.getPermitSectionsCompleted());
		taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
	}
}
