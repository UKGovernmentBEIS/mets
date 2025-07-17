package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.handler;

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
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.service.PermitVariationSubmitService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSaveActionHandlerTest {

	@InjectMocks
    private PermitVariationSaveActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
    private PermitVariationSubmitService permitVariationSubmitService;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION;
		AppUser appUser = AppUser.builder().userId("user").build();
		PermitVariationSaveApplicationRequestTaskActionPayload payload = PermitVariationSaveApplicationRequestTaskActionPayload.builder()
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(false).build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, appUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationSubmitService, times(1)).savePermitVariation(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION);
	}
}
