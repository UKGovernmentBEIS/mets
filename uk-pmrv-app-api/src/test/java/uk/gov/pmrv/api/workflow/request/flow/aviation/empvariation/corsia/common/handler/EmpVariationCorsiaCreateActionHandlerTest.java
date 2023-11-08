package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaCreateActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaCreateActionHandler handler;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;
	
	@Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
	
	@Test
	void process_regulator() {
		Long accountId = 1L;
		RequestCreateActionType type = RequestCreateActionType.EMP_VARIATION_CORSIA;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        PmrvUser pmrvUser = PmrvUser.builder()
        		.roleType(RoleType.REGULATOR)
        		.userId("userId")
        		.build();
        
        EmissionsMonitoringPlanCorsiaDTO dto = createOriginalEmpData();
        
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.EMP_VARIATION_CORSIA)
                .accountId(accountId)
                .requestPayload(EmpVariationCorsiaRequestPayload.builder()
            	        .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            	        .originalEmpContainer(dto.getEmpContainer())
            	        .regulatorAssignee(pmrvUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleType.REGULATOR.name()
                		))
                .requestMetadata(EmpVariationRequestMetadata.builder()
						.type(RequestMetadataType.EMP_VARIATION)
						.initiatorRoleType(RoleType.REGULATOR)
						.build())
                .build();
        
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
        	.thenReturn(Optional.of(dto));
        
        String result = handler.process(accountId, type, payload, pmrvUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
	}
	
	@Test
	void process_operator() {
		Long accountId = 1L;
		RequestCreateActionType type = RequestCreateActionType.EMP_VARIATION_CORSIA;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        PmrvUser pmrvUser = PmrvUser.builder()
        		.roleType(RoleType.OPERATOR)
        		.userId("userId")
        		.build();
        
        EmissionsMonitoringPlanCorsiaDTO dto = createOriginalEmpData();
        
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.EMP_VARIATION_CORSIA)
                .accountId(accountId)
                .requestPayload(EmpVariationCorsiaRequestPayload.builder()
            	        .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
            	        .originalEmpContainer(dto.getEmpContainer())
            	        .operatorAssignee(pmrvUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleType.OPERATOR.name()
                		))
                .requestMetadata(EmpVariationRequestMetadata.builder()
						.type(RequestMetadataType.EMP_VARIATION)
						.initiatorRoleType(RoleType.OPERATOR)
						.build())
                .build();
        
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        when(empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId))
        	.thenReturn(Optional.of(dto));
        
        String result = handler.process(accountId, type, payload, pmrvUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(empQueryService, times(1)).getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId);
	}
	
	@Test
	void getType() {
		assertThat(handler.getType()).isEqualTo(RequestCreateActionType.EMP_VARIATION_CORSIA);
	}
	
	private EmissionsMonitoringPlanCorsiaDTO createOriginalEmpData() {
		UUID attachment = UUID.randomUUID();
		EmissionsMonitoringPlanCorsiaContainer originalEmpContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
    				.abbreviations(EmpAbbreviations.builder().exist(true).build())
    				.build())
    			.empAttachments(Map.of(attachment, "attachment"))
    			.build();
        
		EmissionsMonitoringPlanCorsiaDTO dto = EmissionsMonitoringPlanCorsiaDTO.builder().empContainer(originalEmpContainer).build();
        
		return dto;
	}
}
