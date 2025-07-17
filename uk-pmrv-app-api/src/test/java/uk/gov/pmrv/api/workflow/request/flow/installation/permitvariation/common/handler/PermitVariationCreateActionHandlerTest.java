package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationCreateActionHandlerTest {

	@InjectMocks
    private PermitVariationCreateActionHandler handler;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;

	@Mock
	private PermitQueryService permitQueryService;
	
	@Test
	void process_regulator() {
		Long accountId = 1L;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        AppUser appUser = AppUser.builder()
        		.roleType(RoleTypeConstants.REGULATOR)
        		.userId("userId")
        		.build();

		UUID attachment = UUID.randomUUID();
		PermitContainer originalPermitContainer = PermitContainer.builder()
			.permitType(PermitType.GHGE)
			.permit(Permit.builder()
				.abbreviations(Abbreviations.builder().exist(true).build())
				.monitoringApproaches(MonitoringApproaches.builder()
					.monitoringApproaches(Map.of(
						MonitoringApproachType.INHERENT_CO2, InherentCO2MonitoringApproach.builder().inherentReceivingTransferringInstallations(Collections.emptyList()).build()
					))
					.build())
				.build())
			.permitAttachments(Map.of(attachment, "att"))
			.build();
		
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(PermitVariationRequestPayload.builder()
            	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
						.originalPermitContainer(originalPermitContainer)
            	        .regulatorAssignee(appUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleTypeConstants.REGULATOR
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.initiatorRoleType(RoleTypeConstants.REGULATOR)
						.build())
                .build();

		when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(originalPermitContainer);
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = handler.process(accountId, payload, appUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
		verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
	}
	
	@Test
	void process_operator() {
		Long accountId = 1L;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        AppUser appUser = AppUser.builder()
        		.roleType(RoleTypeConstants.OPERATOR)
        		.userId("userId")
        		.build();
        
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(PermitVariationRequestPayload.builder()
            	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            	        .operatorAssignee(appUser.getUserId())
            	        .build())
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, RoleTypeConstants.OPERATOR
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.initiatorRoleType(RoleTypeConstants.OPERATOR)
						.build())
                .build();
        
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = handler.process(accountId, payload, appUser);
        
        assertThat(result).isEqualTo("1");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void getRequestCreateActionType() {
		assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.PERMIT_VARIATION);
	}
}
