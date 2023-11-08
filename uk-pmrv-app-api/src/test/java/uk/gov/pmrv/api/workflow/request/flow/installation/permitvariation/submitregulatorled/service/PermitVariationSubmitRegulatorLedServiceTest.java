package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationModification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationModificationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationSubmitRegulatorLedService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSubmitRegulatorLedServiceTest {

	@InjectMocks
    private PermitVariationSubmitRegulatorLedService cut;
	
    @Test
    void savePermitVariation() {
    	PermitVariationDetails permitVariationDetails = PermitVariationDetails.builder()
				.reason("reason")
				.modifications(List.of(
						PermitVariationModification.builder().type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES).build(),
						PermitVariationModification.builder().type(PermitVariationModificationType.OTHER_MONITORING_PLAN).otherSummary("summ").build()
						))
				.build();
    	PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload = PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload.builder()
    			.permitVariationDetails(permitVariationDetails)
    			.permitVariationDetailsCompleted(true)
    			.permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.permitType(PermitType.GHGE)
    			.build();
    	
    	RequestTask requestTask = RequestTask.builder()
    			.payload(PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
    					.determination(PermitVariationRegulatorLedGrantDetermination.builder().build())
    					.build())
    			.build();
    	
    	cut.savePermitVariation(taskActionPayload, requestTask);
    	
        PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getPermitVariationDetails()).isEqualTo(taskActionPayload.getPermitVariationDetails());
     	assertThat(requestTaskPayload.getPermitVariationDetailsCompleted()).isEqualTo(taskActionPayload.getPermitVariationDetailsCompleted());
    	assertThat(requestTaskPayload.getPermit()).isEqualTo(taskActionPayload.getPermit());
    	assertThat(requestTaskPayload.getPermitSectionsCompleted()).isEqualTo(taskActionPayload.getPermitSectionsCompleted());
    	assertThat(requestTaskPayload.getPermitType()).isEqualTo(taskActionPayload.getPermitType());
    	assertThat(requestTaskPayload.getDetermination()).isNull();
    }
    
}
