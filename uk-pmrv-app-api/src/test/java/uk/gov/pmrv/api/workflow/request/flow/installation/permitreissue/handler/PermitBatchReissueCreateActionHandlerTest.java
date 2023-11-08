package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitBatchReissueQueryService;

@ExtendWith(MockitoExtension.class)
class PermitBatchReissueCreateActionHandlerTest {

	@InjectMocks
    private PermitBatchReissueCreateActionHandler cut;
    
    @Mock
    private PermitBatchReissueQueryService permitBatchReissueQueryService;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;
    
    @Test
    void getType() {
    	assertThat(cut.getType()).isEqualTo(RequestCreateActionType.PERMIT_BATCH_REISSUE);
    }
    
    @Test
    void process() {
    	Long accountId = null;
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionType type = RequestCreateActionType.PERMIT_BATCH_REISSUE;
    	PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.installationCategories(Set.of(InstallationCategory.A))
				.emitterTypes(Set.of(EmitterType.GHGE))
				.build();
    	BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder()
    			.payloadType(RequestCreateActionPayloadType.PERMIT_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD)
    			.filters(filters)
    			.signatory("signatory")
    			.build();
    	
    	PmrvUser currentUser = PmrvUser.builder()
    			.userId("userId")
    			.firstName("fn").lastName("ln")
    			.authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build()))
    			.build();
    	
    	Map<Long, PermitReissueAccountDetails> accountDetails = Map.of(
    			1L, PermitReissueAccountDetails.builder().installationName("instNam1").operatorName("operName1").permitId("permitId1").build(),
    			2L, PermitReissueAccountDetails.builder().installationName("instNam2").operatorName("operName2").permitId("permitId2").build()
    			);
    	when(permitBatchReissueQueryService.findAccountsByCAAndFilters(ca, filters)).thenReturn(accountDetails);
    	
    	RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_BATCH_REISSUE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .requestPayload(BatchReissueRequestPayload.builder()
            	        .payloadType(RequestPayloadType.PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD)
	            		.filters(filters)
	            		.signatory(payload.getSignatory())
            	        .build())
                .requestMetadata(PermitBatchReissueRequestMetadata.builder()
                		.accountsReports(Map.of(
                				1L, PermitReissueAccountReport.builder().installationName("instNam1").operatorName("operName1").permitId("permitId1").build(),
                				2L, PermitReissueAccountReport.builder().installationName("instNam2").operatorName("operName2").permitId("permitId2").build()
                				))
            	        .submitterId(currentUser.getUserId())
	            		.submitter(currentUser.getFullName())
                		.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
                		.build())
                .processVars(Map.of(BpmnProcessConstants.ACCOUNT_IDS, accountDetails.keySet().stream()
						.collect(Collectors.toSet()),
                		BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = cut.process(accountId, type, payload, currentUser);
        
        assertThat(result).isEqualTo("1");
        verify(permitBatchReissueQueryService, times(1)).findAccountsByCAAndFilters(ca, filters);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
