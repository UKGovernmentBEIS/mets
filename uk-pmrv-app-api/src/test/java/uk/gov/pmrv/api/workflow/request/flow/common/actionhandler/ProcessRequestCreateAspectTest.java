package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByCAValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.service.InstallationAccountOpeningCreateValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReInitiateValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.RequestCreatePermitBatchReissueValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderCreateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessRequestCreateAspectTest {

    private ProcessRequestCreateAspect aspect;
    
    @Mock
    private PermitSurrenderCreateValidator permitSurrenderCreateValidator;

    @Mock
    private InstallationAccountOpeningCreateValidator installationAccountOpeningCreateValidator;
    
    @Mock
    private RequestCreatePermitBatchReissueValidator requestCreatePermitBatchReissueValidator;

    @Mock
    private AerReInitiateValidator aerReInitiateValidator;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private CompetentAuthorityService competentAuthorityService;

    @Spy
    private ArrayList<RequestCreateByAccountValidator> requestCreateByAccountValidators;

    @Spy
    private ArrayList<RequestCreateByRequestValidator> requestCreateByRequestValidators;
    
    @Spy
    private ArrayList<RequestCreateByCAValidator> requestCreateByCAValidators;
    
    @Mock
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
    	requestCreateByAccountValidators.add(permitSurrenderCreateValidator);
    	requestCreateByAccountValidators.add(installationAccountOpeningCreateValidator);
    	requestCreateByCAValidators.add(requestCreatePermitBatchReissueValidator);
        requestCreateByRequestValidators.add(aerReInitiateValidator);
    	
		aspect = new ProcessRequestCreateAspect(requestCreateByAccountValidators, requestCreateByRequestValidators, requestCreateByCAValidators,
				accountQueryService, competentAuthorityService);
    }
    
    @Test
    void process_with_account_id_not_null() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                accountId, type, payload, currentUser
        };
        
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();
        
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);
        when(permitSurrenderCreateValidator.getType()).thenReturn(type);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(validationResult);
        
        aspect.process(joinPoint);
        
        verify(joinPoint, times(1)).getArgs();
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
        verifyNoInteractions(competentAuthorityService);
    }

    @Test
    void process_with_account_id_not_null_and_request_validator() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.AER;
        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .requestId("requestId")
                .build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                accountId, type, payload, currentUser
        };

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(joinPoint.getArgs()).thenReturn(arguments);
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        when(aerReInitiateValidator.getType()).thenReturn(type);
        when(aerReInitiateValidator.validateAction(accountId, payload)).thenReturn(validationResult);

        aspect.process(joinPoint);

        verify(joinPoint, times(1)).getArgs();
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(aerReInitiateValidator, times(1)).getType();
        verify(aerReInitiateValidator, times(1)).validateAction(accountId, payload);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
        verifyNoInteractions(competentAuthorityService);
    }
    
    @Test
    void process_with_account_id_not_null_validation_failed() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                accountId, type, payload, currentUser
        };
        
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder()
                .valid(false)
                .reportedAccountStatus(InstallationAccountStatus.NEW)
                .reportedRequestTypes(Set.of(RequestType.INSTALLATION_ACCOUNT_OPENING))
                .build();
        
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);
        when(permitSurrenderCreateValidator.getType()).thenReturn(type);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(validationResult);
        
        BusinessException be = assertThrows(BusinessException.class, () -> aspect.process(joinPoint));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);
        assertThat(be.getData()[0]).isEqualTo(validationResult);
        
        verify(joinPoint, times(1)).getArgs();
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
        verifyNoInteractions(competentAuthorityService);
    }

    @Test
    void process_with_account_id_not_null_invalid_create_action_for_account_type() {
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                accountId, type, payload, currentUser
        };

        when(joinPoint.getArgs()).thenReturn(arguments);
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.AVIATION);
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.CORSIA);

        when(permitSurrenderCreateValidator.getType()).thenReturn(type);

        BusinessException be = assertThrows(BusinessException.class, () -> aspect.process(joinPoint));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);

        verify(joinPoint, times(1)).getArgs();
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountQueryService, times(1)).getAccountEmissionTradingScheme(accountId);

        verifyNoMoreInteractions(accountQueryService);
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verifyNoMoreInteractions(permitSurrenderCreateValidator);
        verifyNoInteractions(competentAuthorityService);
    }

    @Test
    void process_account_opening_request_type() {
        final RequestCreateActionType type = RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
        final InstallationAccountOpeningSubmitApplicationCreateActionPayload payload = InstallationAccountOpeningSubmitApplicationCreateActionPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                null, type, payload, currentUser
        };

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(joinPoint.getArgs()).thenReturn(arguments);
        when(installationAccountOpeningCreateValidator.getType()).thenReturn(type);
        when(installationAccountOpeningCreateValidator.validateAction(null)).thenReturn(validationResult);

        aspect.process(joinPoint);

        verify(joinPoint, times(1)).getArgs();
        verify(installationAccountOpeningCreateValidator, times(1)).getType();
        verify(installationAccountOpeningCreateValidator, times(1)).validateAction(null);
        verifyNoInteractions(accountQueryService);
        verifyNoInteractions(competentAuthorityService);
    }
    
    @Test
    void process_with_account_id_null_ca_validator() {
        final Long accountId = null;
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_BATCH_REISSUE;
        final BatchReissueRequestCreateActionPayload payload = BatchReissueRequestCreateActionPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId")
        		.authorities(List.of(PmrvAuthority.builder().competentAuthority(competentAuthority).build()))
        		.build();
        final Object[] arguments = new Object[] {
                accountId, type, payload, currentUser
        };
        
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestCreatePermitBatchReissueValidator.getType()).thenReturn(type);
        
        aspect.process(joinPoint);
        
        verify(joinPoint, times(1)).getArgs();
        verifyNoInteractions(accountQueryService, permitSurrenderCreateValidator, installationAccountOpeningCreateValidator, aerReInitiateValidator);
        verify(requestCreatePermitBatchReissueValidator, times(1)).getType();
        verify(requestCreatePermitBatchReissueValidator, times(1)).validateAction(competentAuthority, payload);
        verify(competentAuthorityService, times(1)).exclusiveLockCompetentAuthority(competentAuthority);
    }

    @Test
    void process_with_account_id_not_null_request_unavailable() {
        
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_SURRENDER;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final PmrvUser currentUser = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
            accountId, type, payload, currentUser
        };

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder()
            .isAvailable(false)
            .build();

        when(joinPoint.getArgs()).thenReturn(arguments);
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);
        when(permitSurrenderCreateValidator.getType()).thenReturn(type);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(validationResult);

        BusinessException be = assertThrows(BusinessException.class, () -> aspect.process(joinPoint));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);
        assertThat(be.getData()[0]).isEqualTo(validationResult);

        verify(joinPoint, times(1)).getArgs();
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(permitSurrenderCreateValidator, times(1)).getType();
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        verify(accountQueryService, times(1)).exclusiveLockAccount(accountId);
        verifyNoInteractions(competentAuthorityService);
    }
}
