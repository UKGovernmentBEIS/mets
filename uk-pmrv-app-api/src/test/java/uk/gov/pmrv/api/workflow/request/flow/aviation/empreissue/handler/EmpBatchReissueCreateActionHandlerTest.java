package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.handler;

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

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountReport;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpBatchReissueCreateActionHandlerTest {

	@InjectMocks
    private EmpBatchReissueCreateActionHandler cut;
    
    @Mock
    private EmpBatchReissueQueryService empBatchReissueQueryService;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;
    
    @Test
    void getType() {
    	assertThat(cut.getType()).isEqualTo(RequestCreateActionType.EMP_BATCH_REISSUE);
    }
    
    @Test
    void process() {
    	Long accountId = null;
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionType type = RequestCreateActionType.EMP_BATCH_REISSUE;
    	EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
				.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
				.build();
    	BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder()
    			.payloadType(RequestCreateActionPayloadType.EMP_BATCH_REISSUE_REQUEST_CREATE_ACTION_PAYLOAD)
    			.filters(filters)
    			.signatory("signatory")
    			.build();
    	
    	PmrvUser currentUser = PmrvUser.builder()
    			.userId("userId")
    			.firstName("fn").lastName("ln")
    			.authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build()))
    			.build();
    	
    	Map<Long, EmpReissueAccountDetails> accountDetails = Map.of(
    			1L, EmpReissueAccountDetails.builder().accountName("accountName1").empId("empId1").build(),
    			2L, EmpReissueAccountDetails.builder().accountName("accountName2").empId("empId2").build()
    			);
    	when(empBatchReissueQueryService.findAccountsByCAAndFilters(ca, filters)).thenReturn(accountDetails);
    	
    	RequestParams requestParams = RequestParams.builder()
                .type(RequestType.EMP_BATCH_REISSUE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .requestPayload(BatchReissueRequestPayload.builder()
            	        .payloadType(RequestPayloadType.EMP_BATCH_REISSUE_REQUEST_PAYLOAD)
	            		.filters(filters)
	            		.signatory(payload.getSignatory())
            	        .build())
                .requestMetadata(EmpBatchReissueRequestMetadata.builder()
                		.accountsReports(Map.of(
                				1L, EmpReissueAccountReport.builder().accountName("accountName1").empId("empId1").build(),
                				2L, EmpReissueAccountReport.builder().accountName("accountName2").empId("empId2").build()
                				))
            	        .submitterId(currentUser.getUserId())
	            		.submitter(currentUser.getFullName())
                		.type(RequestMetadataType.EMP_BATCH_REISSUE)
                		.build())
                .processVars(Map.of(BpmnProcessConstants.ACCOUNT_IDS, accountDetails.keySet().stream()
						.collect(Collectors.toSet()),
                		BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());
        
        String result = cut.process(accountId, type, payload, currentUser);
        
        assertThat(result).isEqualTo("1");
        verify(empBatchReissueQueryService, times(1)).findAccountsByCAAndFilters(ca, filters);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
    
}
