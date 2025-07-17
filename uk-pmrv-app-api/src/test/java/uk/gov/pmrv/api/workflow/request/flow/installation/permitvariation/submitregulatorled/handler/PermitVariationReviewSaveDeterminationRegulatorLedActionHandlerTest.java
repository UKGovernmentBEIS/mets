package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler.PermitVariationReviewSaveDeterminationRegulatorLedActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewSaveDeterminationRegulatorLedActionHandlerTest {

	@InjectMocks
    private PermitVariationReviewSaveDeterminationRegulatorLedActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationRegulatorLedService permitVariationRegulatorLedService;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    
    @Test
    void getTypes() {
    	assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED);
    }
    
    @Test
    void process() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED;
        final AppUser appUser = AppUser.builder().build();
        final PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload actionPayload = PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload.builder()
    			.determination(PermitVariationRegulatorLedGrantDetermination.builder()
    					.activationDate(LocalDate.of(LocalDate.now().getYear() + 1, 1, 1))
    					.reason("reason")
    					.logChanges("logChanges")
    					.reasonTemplate(PermitVariationReasonTemplate.FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR)
    					.build())
    			.build();
        
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId)
        		.request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
        		.build();
        
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        cut.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationRegulatorLedService, times(1)).saveDeterminationRegulatorLed(actionPayload, requestTask);
    }
}
