package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewSaveActionHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsReviewSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationUkEtsReviewService empVariationUkEtsReviewService;
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW);
	}
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW;
		AppUser appUser = AppUser.builder().build();
		EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload payload = EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload
				.builder().build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, appUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(empVariationUkEtsReviewService, times(1)).saveEmpVariation(payload, requestTask);
	}
}
