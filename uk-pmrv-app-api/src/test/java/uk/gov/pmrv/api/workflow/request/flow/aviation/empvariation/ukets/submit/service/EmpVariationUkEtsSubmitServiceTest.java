package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsChangeType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.validator.EmpVariationUkEtsDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.mapper.EmpVariationUkEtsSubmitMapper;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSubmitServiceTest {

	@InjectMocks
    private EmpVariationUkEtsSubmitService service;
	
	@Mock
    private RequestService requestService;
	
	@Mock
    private RequestAviationAccountQueryService queryService;

	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
	
	@Mock
    private EmpVariationUkEtsDetailsValidator detailsValidator;

    @Test
    void saveEmpVariation() {
    	EmpVariationUkEtsDetails details = EmpVariationUkEtsDetails
    			.builder()
    			.reason("test reason")
    			.changes(List.of(
    					EmpVariationUkEtsChangeType.LEGAL_ENTITY_NAME, 
    					EmpVariationUkEtsChangeType.NEW_FUEL_TYPE))
    			.build();
    	
    	EmpVariationUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload = EmpVariationUkEtsSaveApplicationRequestTaskActionPayload.builder()
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts
						.builder()
						.abbreviations(EmpAbbreviations.builder().exist(false).build())
						.build())
    			.empVariationDetails(details)
    			.empVariationDetailsCompleted(Boolean.TRUE)
    			.empSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("rev_section1", true))
    			.build();
    	
    	RequestTask requestTask = RequestTask.builder()
    			.payload(EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
    					.build())
    			.build();
    	
    	service.saveEmpVariation(taskActionPayload, requestTask);
    	
        EmpVariationUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = (EmpVariationUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
    	assertThat(requestTaskPayload.getEmissionsMonitoringPlan()).isEqualTo(taskActionPayload.getEmissionsMonitoringPlan());
    	assertThat(requestTaskPayload.getEmpSectionsCompleted()).isEqualTo(taskActionPayload.getEmpSectionsCompleted());
    	assertThat(requestTaskPayload.getEmpVariationDetails()).isEqualTo(taskActionPayload.getEmpVariationDetails());
     	assertThat(requestTaskPayload.getEmpVariationDetailsCompleted()).isEqualTo(taskActionPayload.getEmpVariationDetailsCompleted());
     	assertThat(requestTaskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void submitEmpVariation() {
    	AppUser authUser = AppUser.builder().userId("user1").build();
    	UUID att1UUID = UUID.randomUUID();
    	Request request = Request.builder()
    			.accountId(1L)
    			.id("1")
    			.payload(EmpVariationUkEtsRequestPayload.builder().build())
    			.build();
    	
    	EmpVariationUkEtsDetails details = EmpVariationUkEtsDetails
    			.builder()
    			.reason("test reason")
    			.changes(List.of(
    					EmpVariationUkEtsChangeType.LEGAL_ENTITY_NAME, 
    					EmpVariationUkEtsChangeType.NEW_FUEL_TYPE))
    			.build();
    	
    	EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts
    			.builder()
    			.abbreviations(EmpAbbreviations.builder()
    					.exist(false).build())
    			.operatorDetails(EmpOperatorDetails.builder()
    					.operatorName("op1")
    					.crcoCode("crco1")
    					.build())
				.build();

        EmpVariationUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationSubmitRequestTaskPayload.builder()
			.emissionsMonitoringPlan(emp)
			.empVariationDetails(details)
			.empVariationDetailsCompleted(Boolean.TRUE)
			.empSectionsCompleted(Map.of("section1", List.of(true, false)))
			.reviewSectionsCompleted(Map.of("rev_section1", true))
			.empAttachments(Map.of(att1UUID, "att1"))
			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();
    	RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().operatorName("op12").build();
 
    	
    	when(queryService.getAccountInfo(request.getAccountId())).thenReturn(accountInfo);
    	
    	service.submitEmpVariation(requestTask, authUser);
    	
    	verify(empUkEtsValidatorService, times(1)).validateEmissionsMonitoringPlan(Mappers
				.getMapper(EmpVariationUkEtsMapper.class).toEmissionsMonitoringPlanUkEtsContainer(requestTaskPayload));
        EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        assertThat(requestPayload.getEmissionsMonitoringPlan()).isEqualTo(emp);
        assertThat(requestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", List.of(true, false)));
        assertThat(requestPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));
        assertThat(requestPayload.getEmpVariationDetails()).isEqualTo(details);
        assertThat(requestPayload.getEmpVariationDetailsCompleted()).isTrue();
        assertThat(requestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("rev_section1", true));
        verify(detailsValidator, times(1)).validate(details);
        
		EmpVariationUkEtsApplicationSubmittedRequestActionPayload actionPayload = Mappers
				.getMapper(EmpVariationUkEtsSubmitMapper.class).toEmpVariationUkEtsApplicationSubmittedRequestActionPayload(
						(EmpVariationUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload(),
						accountInfo);
        verify(queryService, times(1)).getAccountInfo(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.EMP_VARIATION_UKETS_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
