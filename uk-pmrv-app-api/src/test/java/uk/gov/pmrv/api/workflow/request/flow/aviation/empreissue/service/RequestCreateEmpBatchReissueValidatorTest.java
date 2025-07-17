package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;

@ExtendWith(MockitoExtension.class)
class RequestCreateEmpBatchReissueValidatorTest {

	@InjectMocks
    private RequestCreateEmpBatchReissueValidator cut;
    
    @Mock
    private RequestQueryService requestQueryService;
    
    @Mock
    private EmpBatchReissueQueryService empBatchReissueQueryService;
    
    @Test
    void getType() {
    	assertThat(cut.getType()).isEqualTo(RequestCreateActionType.EMP_BATCH_REISSUE);
    }
    
    @Test
    void validateAction() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
				.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
				.build();
    	BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder()
    			.filters(filters)
    			.build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority))
    		.thenReturn(false);
    	
    	when(empBatchReissueQueryService.existAccountsByCAAndFilters(competentAuthority, filters))
			.thenReturn(true);
    	
    	cut.validateAction(competentAuthority, payload);
    	
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority);
    	verify(empBatchReissueQueryService, times(1)).existAccountsByCAAndFilters(competentAuthority, filters);
    }
    
    @Test
    void validateAction_in_progress_exist_error() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
				.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
				.build();
    	BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder()
    			.filters(filters)
    			.build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority))
    		.thenReturn(true);
    	
    	BusinessException be = assertThrows(BusinessException.class, () -> cut.validateAction(competentAuthority, payload));
    	assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BATCH_REISSUE_IN_PROGRESS_REQUEST_EXISTS);
    	
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority);
    	verifyNoInteractions(empBatchReissueQueryService);
    }
    
    @Test
    void validateAction_zero_emitters_error() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
				.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
				.build();
    	BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder()
    			.filters(filters)
    			.build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority))
    		.thenReturn(false);
    	
    	when(empBatchReissueQueryService.existAccountsByCAAndFilters(competentAuthority, filters))
			.thenReturn(false);
    	
    	BusinessException be = assertThrows(BusinessException.class, () -> cut.validateAction(competentAuthority, payload));
    	assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BATCH_REISSUE_ZERO_EMITTERS_SELECTED);
    	
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority);
    	verify(empBatchReissueQueryService, times(1)).existAccountsByCAAndFilters(competentAuthority, filters);
    }
}
