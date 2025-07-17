package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueAddCompletedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class EmpReissueDoReissueServiceTest {

	@InjectMocks
	private EmpReissueDoReissueService cut;

	@Mock
	private RequestService requestService;
	
	@Mock
	private EmpReissueAccountValidationService empReissueAccountValidationService;
	
	@Mock
	private EmissionsMonitoringPlanService emissionsMonitoringPlanService;
	
	@Mock
	private EmpReissueUpdatePayloadConsolidationNumberService empReissueUpdatePayloadConsolidationNumberService;
	
	@Mock
	private EmpReissueGenerateDocumentsService empReissueGenerateDocumentsService;
	
	@Mock
	private ReissueAddCompletedRequestActionService reissueAddCompletedRequestActionService;
	
	@Mock
	private ReissueOfficialNoticeService reissueOfficialNoticeService;
	
	@Test
	void doReissue() {
		String requestId = "1";
		Long accountId = 1L;
		Request request = Request.builder()
				.type(RequestType.EMP_REISSUE)
				.accountId(accountId)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(empReissueAccountValidationService.isAccountApplicableToReissue(request)).thenReturn(true);
		
		cut.doReissue(requestId);
		
		assertThat(request.getSubmissionDate()).isNotNull();
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(empReissueAccountValidationService, times(1)).isAccountApplicableToReissue(request);
		verify(emissionsMonitoringPlanService, times(1)).incrementEmpConsolidationNumber(accountId);
		verify(empReissueUpdatePayloadConsolidationNumberService, times(1)).updateRequestPayloadConsolidationNumber(request);
		verify(empReissueGenerateDocumentsService, times(1)).generateDocuments(request);
		verify(reissueAddCompletedRequestActionService, times(1)).add(requestId);
		verify(reissueOfficialNoticeService, times(1)).sendOfficialNotice(request);
	}
	
	@Test
	void doReissue_account_not_applicable() {
		String requestId = "1";
		Long accountId = 1L;
		Request request = Request.builder()
				.type(RequestType.EMP_REISSUE)
				.accountId(accountId)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(empReissueAccountValidationService.isAccountApplicableToReissue(request)).thenReturn(false);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.doReissue(requestId));
		
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.REISSUE_ACCOUNT_NOT_APPLICABLE);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(empReissueAccountValidationService, times(1)).isAccountApplicableToReissue(request);
		verifyNoInteractions(emissionsMonitoringPlanService, empReissueUpdatePayloadConsolidationNumberService, empReissueGenerateDocumentsService,
				reissueAddCompletedRequestActionService, reissueOfficialNoticeService);
	}
}
