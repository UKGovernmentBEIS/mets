package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewSaveActionHandlerTest {

	@InjectMocks
    private PermitVariationReviewSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private PermitVariationReviewService permitVariationReviewService;
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW);
	}
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW;
		AppUser appUser = AppUser.builder().build();
		PermitVariationSaveApplicationReviewRequestTaskActionPayload payload = PermitVariationSaveApplicationReviewRequestTaskActionPayload
				.builder().build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, appUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationReviewService, times(1)).savePermitVariation(payload, requestTask);
	}
	
}
