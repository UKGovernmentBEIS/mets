package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.service;

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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.validator.EmpVariationCorsiaDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.mapper.EmpVariationCorsiaSubmitMapper;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSubmitServiceTest {

	@InjectMocks
    private EmpVariationCorsiaSubmitService service;
	
	@Mock
    private RequestService requestService;
	
	@Mock
    private RequestAviationAccountQueryService queryService;

	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
	
	@Mock
    private EmpVariationCorsiaDetailsValidator detailsValidator;

    @Test
    void saveEmpVariation() { 	
    	EmpVariationCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload = EmpVariationCorsiaSaveApplicationRequestTaskActionPayload.builder()
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.build())
    			.empVariationDetails(buildVariationDetails())
    			.empVariationDetailsCompleted(Boolean.TRUE)
    			.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("rev_section1", true))
    			.build();
    	
    	RequestTask requestTask = RequestTask.builder()
    			.payload(EmpVariationCorsiaApplicationSubmitRequestTaskPayload.builder()
    					.build())
    			.build();
    	
    	service.saveEmpVariation(taskActionPayload, requestTask);
    	
        EmpVariationCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = (EmpVariationCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();
    	assertThat(requestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(taskActionPayload.getEmissionsMonitoringPlan());
    	assertThat(requestTaskPayload.getEmpSectionsCompleted()).isEqualTo(taskActionPayload.getEmpSectionsCompleted());
    	assertThat(requestTaskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    	assertThat(requestTaskPayload.getEmpVariationDetails()).isEqualTo(taskActionPayload.getEmpVariationDetails());
     	assertThat(requestTaskPayload.getEmpVariationDetailsCompleted()).isEqualTo(taskActionPayload.getEmpVariationDetailsCompleted());
    }
    
    @Test
    void submitEmpVariation() {
    	AppUser authUser = AppUser.builder().userId("user1").build();
    	UUID att1UUID = UUID.randomUUID();
    	Request request = Request.builder()
    			.accountId(1L)
    			.id("1")
    			.payload(EmpVariationCorsiaRequestPayload.builder().build())
    			.build();
    	
    	EmissionsMonitoringPlanCorsia emp = EmissionsMonitoringPlanCorsia
    			.builder()
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(false).build())
    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
    					.operatorName("op1")
    					.build())
				.build();

        EmpVariationCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = 
        		EmpVariationCorsiaApplicationSubmitRequestTaskPayload.builder()
					.emissionsMonitoringPlan(emp)
					.empVariationDetails(buildVariationDetails())
	    			.empVariationDetailsCompleted(Boolean.TRUE)
					.empSectionsCompleted(Map.of("section1", List.of(true, false)))
					.empAttachments(Map.of(att1UUID, "att1"))
					.reviewSectionsCompleted(Map.of("rev_section1", true))
					.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();
    	RequestAviationAccountInfo accountInfo = 
    			RequestAviationAccountInfo.builder().operatorName("op12").build();
 
    	
    	when(queryService.getAccountInfo(request.getAccountId())).thenReturn(accountInfo);
    	
    	service.submitEmpVariation(requestTask, authUser);
    	
    	verify(empCorsiaValidatorService, times(1)).validateEmissionsMonitoringPlan(Mappers
				.getMapper(EmpVariationCorsiaMapper.class).toEmissionsMonitoringPlanCorsiaContainer(requestTaskPayload));
        EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        assertThat(requestPayload.getEmissionsMonitoringPlan()).isEqualTo(emp);
        assertThat(requestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", List.of(true, false)));
        assertThat(requestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));
        assertThat(requestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("rev_section1", true));
        assertThat(requestPayload.getEmpVariationDetails()).isEqualTo(requestPayload.getEmpVariationDetails());
        assertThat(requestPayload.getEmpVariationDetailsCompleted()).isTrue();
        verify(detailsValidator, times(1)).validate(requestPayload.getEmpVariationDetails());
        
		EmpVariationCorsiaApplicationSubmittedRequestActionPayload actionPayload = Mappers
				.getMapper(EmpVariationCorsiaSubmitMapper.class).toEmpVariationCorsiaApplicationSubmittedRequestActionPayload(
						(EmpVariationCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload(),
						accountInfo);
        verify(queryService, times(1)).getAccountInfo(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, 
        		RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED, authUser.getUserId());
    }
    
    private EmpVariationCorsiaDetails buildVariationDetails() {
		return EmpVariationCorsiaDetails
    			.builder()
    			.reason("test reason")
    			.changes(List.of(
    					EmpVariationCorsiaChangeType.AEROPLANE_OPERATOR_NAME, 
    					EmpVariationCorsiaChangeType.FUEL_USE_MONITORING_METHODOLOGY))
    			.build();
	}
}
