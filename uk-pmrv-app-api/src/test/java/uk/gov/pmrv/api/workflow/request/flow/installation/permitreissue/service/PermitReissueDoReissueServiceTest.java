package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

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

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueAddCompletedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitReissueDoReissueServiceTest {

	@InjectMocks
	private PermitReissueDoReissueService cut;

	@Mock
	private RequestService requestService;
	
	@Mock
	private PermitReissueAccountValidationService permitReissueAccountValidationService;
	
	@Mock
	private PermitService permitService;
	
	@Mock
	private PermitReissueUpdatePayloadConsolidationNumberService reissueUpdatePayloadConsolidationNumberService;
	
	@Mock
	private PermitReissueGenerateDocumentsService permitReissueGenerateDocumentsService;
	
	@Mock
	private ReissueAddCompletedRequestActionService reissueAddCompletedRequestActionService;
	
	@Mock
	private ReissueOfficialNoticeService reissueOfficialNoticeService;
	
	@Test
	void doReissue() {
		String requestId = "1";
		Long accountId = 1L;
		Request request = Request.builder()
				.type(RequestType.PERMIT_REISSUE)
				.accountId(accountId)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(permitReissueAccountValidationService.isAccountApplicableToReissue(request)).thenReturn(true);
		
		cut.doReissue(requestId);
		
		assertThat(request.getSubmissionDate()).isNotNull();
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(permitReissueAccountValidationService, times(1)).isAccountApplicableToReissue(request);
		verify(permitService, times(1)).incrementPermitConsolidationNumber(accountId);
		verify(reissueUpdatePayloadConsolidationNumberService, times(1)).updateRequestPayloadConsolidationNumber(request);
		verify(permitReissueGenerateDocumentsService, times(1)).generateDocuments(request);
		verify(reissueAddCompletedRequestActionService, times(1)).add(requestId);
		verify(reissueOfficialNoticeService, times(1)).sendOfficialNotice(request);
	}
	
	@Test
	void doReissue_account_not_applicable() {
		String requestId = "1";
		Long accountId = 1L;
		Request request = Request.builder()
				.type(RequestType.PERMIT_REISSUE)
				.accountId(accountId)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(permitReissueAccountValidationService.isAccountApplicableToReissue(request)).thenReturn(false);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.doReissue(requestId));
		
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.REISSUE_ACCOUNT_NOT_APPLICABLE);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(permitReissueAccountValidationService, times(1)).isAccountApplicableToReissue(request);
		verifyNoInteractions(permitService, permitReissueGenerateDocumentsService,
				reissueAddCompletedRequestActionService, reissueOfficialNoticeService, reissueUpdatePayloadConsolidationNumberService);
	}
	
}
