package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler.PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedActionHandlerTest {

	@InjectMocks
    private PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationRegulatorLedService permitVariationRegulatorLedService;

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED);
    }

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED;
        AppUser appUser = AppUser.builder().build();
        PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload = PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload
            .builder().build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        cut.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationRegulatorLedService, times(1)).saveDetailsReviewGroupDecisionRegulatorLed(payload, requestTask);
    }
}
