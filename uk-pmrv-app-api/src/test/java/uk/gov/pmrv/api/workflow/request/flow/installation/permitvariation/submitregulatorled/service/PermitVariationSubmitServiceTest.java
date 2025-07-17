package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationModification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationModificationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.mapper.PermitVariationSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.service.PermitVariationSubmitService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSubmitServiceTest {

	@InjectMocks
    private PermitVariationSubmitService service;
	
	@Mock
    private RequestService requestService;

    @Mock
    private PermitValidatorService permitValidatorService;
    
    @Mock 
    private PermitVariationDetailsValidator permitVariationDetailsValidator;
    
    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void savePermitVariation() {
    	PermitVariationDetails permitVariationDetails = PermitVariationDetails.builder()
				.reason("reason")
				.modifications(List.of(
						PermitVariationModification.builder().type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES).build(),
						PermitVariationModification.builder().type(PermitVariationModificationType.OTHER_MONITORING_PLAN).otherSummary("summ").build()
						))
				.build();
    	PermitVariationSaveApplicationRequestTaskActionPayload taskActionPayload = PermitVariationSaveApplicationRequestTaskActionPayload.builder()
    			.permitVariationDetails(permitVariationDetails)
    			.permitVariationDetailsCompleted(true)
    			.permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("rev_section1", true))
    			.build();
    	
    	RequestTask requestTask = RequestTask.builder()
    			.payload(PermitVariationApplicationSubmitRequestTaskPayload.builder()
    					.build())
    			.build();
    	
    	service.savePermitVariation(taskActionPayload, requestTask);
    	
        PermitVariationApplicationSubmitRequestTaskPayload requestTaskPayload = (PermitVariationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getPermitVariationDetails()).isEqualTo(taskActionPayload.getPermitVariationDetails());
     	assertThat(requestTaskPayload.getPermitVariationDetailsCompleted()).isEqualTo(taskActionPayload.getPermitVariationDetailsCompleted());
    	assertThat(requestTaskPayload.getPermit()).isEqualTo(taskActionPayload.getPermit());
    	assertThat(requestTaskPayload.getPermitSectionsCompleted()).isEqualTo(taskActionPayload.getPermitSectionsCompleted());
    	assertThat(requestTaskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void submitPermitVariation() {
    	AppUser authUser = AppUser.builder().userId("user1").build();
    	UUID att1UUID = UUID.randomUUID();
    	Request request = Request.builder()
    			.accountId(1L)
    			.id("1")
    			.payload(PermitVariationRequestPayload.builder().build())
    			.build();
    	Permit permit = Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build();
    	InstallationOperatorDetails installationOperatorDetails1 = InstallationOperatorDetails.builder()
				.installationName("installationName1")
				.build();
    	InstallationOperatorDetails installationOperatorDetails2 = InstallationOperatorDetails.builder()
				.installationName("installationName2")
				.build();
        PermitVariationDetails permitVariationDetails = PermitVariationDetails.builder()
				.reason("reason")
				.modifications(List.of(
						PermitVariationModification.builder().type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES).build(),
						PermitVariationModification.builder().type(PermitVariationModificationType.OTHER_MONITORING_PLAN).otherSummary("summ").build()
						))
				.build();
        PermitVariationApplicationSubmitRequestTaskPayload requestTaskPayload = PermitVariationApplicationSubmitRequestTaskPayload.builder()
			.permitVariationDetails(permitVariationDetails)
			.permitType(PermitType.GHGE)
			.permit(permit)
			.installationOperatorDetails(installationOperatorDetails1)
			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
			.permitVariationDetailsCompleted(Boolean.TRUE)
			.permitAttachments(Map.of(att1UUID, "att1"))
			.reviewSectionsCompleted(Map.of("rev_section1", true))
			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
				.thenReturn(installationOperatorDetails2);
    	
    	service.submitPermitVariation(requestTask, authUser);
    	
    	PermitContainer permitContainer = PermitContainer.builder()
    			.permitType(PermitType.GHGE)
				.permit(permit)
				.installationOperatorDetails(installationOperatorDetails2)
    			.permitAttachments(Map.of(att1UUID, "att1"))
    			.build();
    	
    	verify(permitValidatorService, times(1)).validatePermit(permitContainer);
        PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        assertThat(requestPayload.getPermitVariationDetails()).isEqualTo(permitVariationDetails);
        assertThat(requestPayload.getPermitType()).isEqualTo(PermitType.GHGE);
        assertThat(requestPayload.getPermit()).isEqualTo(permit);
        assertThat(requestPayload.getPermitSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", List.of(true, false)));
        assertThat(requestPayload.getPermitVariationDetailsCompleted()).isTrue();
        assertThat(requestPayload.getPermitAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));
        assertThat(requestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("rev_section1", true));
        
		PermitVariationApplicationSubmittedRequestActionPayload actionPayload = Mappers
				.getMapper(PermitVariationSubmitMapper.class).toPermitVariationApplicationSubmittedRequestActionPayload(
						(PermitVariationApplicationSubmitRequestTaskPayload) requestTask.getPayload(),
						installationOperatorDetails2);
        verify(permitValidatorService, times(1)).validatePermit(Mappers.getMapper(PermitVariationSubmitMapper.class).toPermitContainer(requestTaskPayload, installationOperatorDetails2));
        verify(permitVariationDetailsValidator, times(1)).validate(permitVariationDetails);        
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_SUBMITTED, authUser.getUserId());
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(request.getAccountId());
    }
}
