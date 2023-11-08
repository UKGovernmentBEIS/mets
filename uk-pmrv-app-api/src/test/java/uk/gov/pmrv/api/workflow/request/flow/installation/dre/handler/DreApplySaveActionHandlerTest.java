package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyService;

@ExtendWith(MockitoExtension.class)
class DreApplySaveActionHandlerTest {

	@InjectMocks
    private DreApplySaveActionHandler cut;

    @Mock
    private DreApplyService dreApplyService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.DRE_SAVE_APPLICATION;
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		DreSaveApplicationRequestTaskActionPayload payload = DreSaveApplicationRequestTaskActionPayload.builder()
				.dre(Dre.builder()
						.determinationReason(DreDeterminationReason.builder().build())
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder().id(1L).build();
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(dreApplyService, times(1)).applySaveAction(payload, requestTask);
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.DRE_SAVE_APPLICATION);
	}
}
