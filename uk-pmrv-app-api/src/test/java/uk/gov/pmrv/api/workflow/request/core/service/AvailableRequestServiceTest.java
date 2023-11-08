package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailableRequestServiceTest {

    private AvailableRequestService availableRequestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;

    @Mock
    private AvailableRequestServiceTest.TestRequestCreateValidatorA requestCreateValidatorA;

    @Mock
    private AvailableRequestServiceTest.TestRequestCreateValidatorB requestCreateValidatorB;

    @Mock
    private AvailableRequestServiceTest.TestRequestCreateValidatorC requestCreateValidatorC;

    @Mock
    private EnabledWorkflowValidator enabledWorkflowValidator;

    @Mock
    private AccountQueryService accountQueryService;

    @BeforeEach
    public void setUp() {
        ArrayList<RequestCreateByRequestValidator> requestCreateByRequestValidators = new ArrayList<>();
        requestCreateByRequestValidators.add(requestCreateValidatorC);

        ArrayList<RequestCreateByAccountValidator> requestCreateByAccountValidators = new ArrayList<>();
        requestCreateByAccountValidators.add(requestCreateValidatorA);
        requestCreateByAccountValidators.add(requestCreateValidatorB);

        availableRequestService = new AvailableRequestService(requestRepository, accountRequestAuthorizationResourceService,
                requestCreateByAccountValidators, requestCreateByRequestValidators, enabledWorkflowValidator, accountQueryService);
    }

    @Test
    void getAvailableAccountWorkflows() {
        when(enabledWorkflowValidator.isWorkflowEnabled(any(RequestType.class))).thenReturn(true);

        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;
        final AccountType accountType = AccountType.INSTALLATION;
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        final HashSet<String> actionTypes = new HashSet<>();
        actionTypes.add(RequestCreateActionType.AER.name());
        actionTypes.add(RequestCreateActionType.PERMIT_SURRENDER.name());
        actionTypes.add(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        //action type irrelevant to account type
        actionTypes.add(RequestCreateActionType.EMP_VARIATION_UKETS.name());

        when(accountQueryService.getAccountType(accountId)).thenReturn(accountType);
        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId))
                .thenReturn(actionTypes);
        when(requestCreateValidatorA.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(requestCreateValidatorA.validateAction(accountId)).thenReturn(result);

        final Map<RequestCreateActionType, RequestCreateValidationResult> availableWorkflows =
                availableRequestService.getAvailableAccountWorkflows(accountId, user);

        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountRequestAuthorizationResourceService, times(1))
                .findRequestCreateActionsByAccountId(user, accountId);
        verify(requestCreateValidatorA, times(1)).validateAction(accountId);

        assertThat(availableWorkflows).containsExactly(Map.entry(RequestCreateActionType.PERMIT_SURRENDER, result));
    }

    @Test
    void getAvailableAccountWorkflows_exclude_disallowed_workflows() {
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_VARIATION)).thenReturn(true);
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_SURRENDER)).thenReturn(false);
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.INSTALLATION_ACCOUNT_OPENING)).thenReturn(true);

        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;
        final AccountType accountType = AccountType.INSTALLATION;
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        final HashSet<String> actionTypes = new HashSet<>();
        actionTypes.add(RequestCreateActionType.PERMIT_VARIATION.name());
        actionTypes.add(RequestCreateActionType.PERMIT_SURRENDER.name());
        actionTypes.add(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        when(accountQueryService.getAccountType(accountId)).thenReturn(accountType);
        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId))
                .thenReturn(actionTypes);
        when(requestCreateValidatorA.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(requestCreateValidatorB.getType()).thenReturn(RequestCreateActionType.PERMIT_VARIATION);
        when(requestCreateValidatorB.validateAction(accountId)).thenReturn(result);

        final Map<RequestCreateActionType, RequestCreateValidationResult> availableWorkflows =
                availableRequestService.getAvailableAccountWorkflows(accountId, user);

        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountRequestAuthorizationResourceService, times(1))
                .findRequestCreateActionsByAccountId(user, accountId);
        verify(requestCreateValidatorB, times(1)).validateAction(accountId);
        verify(requestCreateValidatorA, times(0)).validateAction(accountId);

        assertThat(availableWorkflows).containsExactly(Map.entry(RequestCreateActionType.PERMIT_VARIATION, result));
    }

    @Test
    void getAvailableAccountWorkflows_excludeNotAvailable() {
        when(enabledWorkflowValidator.isWorkflowEnabled(any(RequestType.class))).thenReturn(true);

        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;
        final AccountType accountType = AccountType.INSTALLATION;
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().isAvailable(false).build();
        final HashSet<String> actionTypes = new HashSet<>();
        actionTypes.add(RequestCreateActionType.AER.name());
        actionTypes.add(RequestCreateActionType.PERMIT_SURRENDER.name());
        actionTypes.add(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        //action type irrelevant to account type
        actionTypes.add(RequestCreateActionType.EMP_VARIATION_UKETS.name());

        when(accountQueryService.getAccountType(accountId)).thenReturn(accountType);
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId))
                .thenReturn(actionTypes);
        when(requestCreateValidatorA.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(requestCreateValidatorA.validateAction(accountId)).thenReturn(result);

        final Map<RequestCreateActionType, RequestCreateValidationResult> availableWorkflows =
                availableRequestService.getAvailableAccountWorkflows(accountId, user);

        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountQueryService, times(1)).getAccountEmissionTradingScheme(accountId);
        verify(accountRequestAuthorizationResourceService, times(1))
                .findRequestCreateActionsByAccountId(user, accountId);
        verify(requestCreateValidatorA, times(1)).validateAction(accountId);

        assertThat(availableWorkflows).isEmpty();
    }

    @Test
    void getAvailableAerWorkflows() {
        final String requestId = "AEM-1";
        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;

        final Set<String> actionTypes = Set.of(RequestCreateActionType.AER.name(),
                RequestCreateActionType.PERMIT_SURRENDER.name(), RequestCreateActionType.EMP_VARIATION_UKETS.name());

        final Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .build();

        final ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD)
                .requestId(requestId)
                .build();
        final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId)).thenReturn(actionTypes);
        when(enabledWorkflowValidator.isWorkflowEnabled(any(RequestType.class))).thenReturn(true);
        when(requestCreateValidatorC.getType()).thenReturn(RequestCreateActionType.AER);
        when(requestCreateValidatorC.validateAction(accountId, payload)).thenReturn(validationResult);

        // Invoke
        final Map<RequestCreateActionType, RequestCreateValidationResult> actual = availableRequestService
                .getAvailableAerWorkflows(requestId, user);

        // Verify
        assertThat(actual).containsExactly(Map.entry(RequestCreateActionType.AER, validationResult));
        verify(requestRepository, times(1)).findById(requestId);
        verify(accountQueryService, times(1)).getAccountType(accountId);
        verify(accountQueryService, times(1)).getAccountEmissionTradingScheme(accountId);
        verify(accountRequestAuthorizationResourceService, times(1)).findRequestCreateActionsByAccountId(user, accountId);
        verify(enabledWorkflowValidator, times(3)).isWorkflowEnabled(any(RequestType.class));
        verify(requestCreateValidatorC, times(1)).getType();
        verify(requestCreateValidatorC, times(1)).validateAction(accountId, payload);
    }

    @Test
    void getAvailableAerWorkflows_request_not_found() {
        final String requestId = "AEM-1";
        final PmrvUser user = PmrvUser.builder().userId("user").build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                availableRequestService.getAvailableAerWorkflows(requestId, user));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(requestRepository, times(1)).findById(requestId);
        verify(accountQueryService, never()).getAccountType(anyLong());
        verify(accountRequestAuthorizationResourceService, never()).findRequestCreateActionsByAccountId(any(), anyLong());
        verify(enabledWorkflowValidator, never()).isWorkflowEnabled(any());
        verify(requestCreateValidatorC, never()).getType();
        verify(requestCreateValidatorC, never()).validateAction(anyLong(), any());
    }

    private static class TestRequestCreateValidatorA implements RequestCreateByAccountValidator {

        @Override
        public RequestCreateValidationResult validateAction(Long accountId) {
            return null;
        }

        @Override
        public RequestCreateActionType getType() {
            return null;
        }
    }

    private static class TestRequestCreateValidatorB implements RequestCreateByAccountValidator {

        @Override
        public RequestCreateValidationResult validateAction(Long accountId) {
            return null;
        }

        @Override
        public RequestCreateActionType getType() {
            return null;
        }
    }

    private static class TestRequestCreateValidatorC implements RequestCreateByRequestValidator<ReportRelatedRequestCreateActionPayload> {

        @Override
        public RequestCreateValidationResult validateAction(Long accountId, ReportRelatedRequestCreateActionPayload payload) {
            return null;
        }

        @Override
        public RequestCreateActionType getType() {
            return null;
        }
    }
}
