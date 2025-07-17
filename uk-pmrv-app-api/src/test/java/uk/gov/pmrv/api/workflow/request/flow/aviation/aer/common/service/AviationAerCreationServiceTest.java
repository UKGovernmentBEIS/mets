package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCreationServiceTest {

    @InjectMocks
    private AviationAerCreationService aviationAerCreationService;

    @Spy
    private ArrayList<AviationAerCreationRequestParamsBuilderService> aviationAerCreationRequestParamsBuilderServices;

    @Mock
    private AviationAerUkEtsCreationRequestParamsBuilderService aviationAerUkEtsCreationRequestParamsBuilderService;

    @Mock
    private AviationAerCorsiaCreationRequestParamsBuilderService aviationAerCorsiaCreationRequestParamsBuilderService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AviationAerCreationValidatorService aviationAerCreationValidatorService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private AviationAerReportingObligationService aviationAerReportingObligationService;

    @BeforeEach
    void setUp() {
        aviationAerCreationRequestParamsBuilderServices.add(aviationAerUkEtsCreationRequestParamsBuilderService);
        aviationAerCreationRequestParamsBuilderServices.add(aviationAerCorsiaCreationRequestParamsBuilderService);
    }

    @Test
    void createRequestAviationAer_when_ukets_for_account_reporting_status_requiredToReport() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder()
            .emissionTradingScheme(emissionTradingScheme)
            .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .build();
        RequestParams requestParams = RequestParams.builder().build();

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(aviationAerCreationValidatorService.validateAccountStatus(accountId)).thenReturn(validationResult);
        when(aviationAerCreationValidatorService.validateReportingYear(eq(accountId), any())).thenReturn(validationResult);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(aviationAerUkEtsCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);
        when(aviationAerUkEtsCreationRequestParamsBuilderService.buildRequestParams(eq(accountId), any())).thenReturn(requestParams);

        aviationAerCreationService.createRequestAviationAer(accountId);

        verify(aviationAerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(aviationAerCreationValidatorService, times(1)).validateReportingYear(eq(accountId), any());
        verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(aviationAerUkEtsCreationRequestParamsBuilderService, times(1)).buildRequestParams(eq(accountId), any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verifyNoInteractions(aviationAerReportingObligationService);
    }

    @Test
    void createRequestAviationAer_when_corsia_for_account_reporting_status_requiredToReport() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingSchemeUkEts = EmissionTradingScheme.UK_ETS_AVIATION;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder()
            .emissionTradingScheme(emissionTradingScheme)
            .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .build();

        RequestParams requestParams = RequestParams.builder().build();

        RequestCreateValidationResult validResult = RequestCreateValidationResult.builder().valid(true).build();

        when(aviationAerCreationValidatorService.validateAccountStatus(accountId)).thenReturn(validResult);
        when(aviationAerCreationValidatorService.validateReportingYear(eq(accountId), any())).thenReturn(validResult);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(aviationAerUkEtsCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingSchemeUkEts);
        when(aviationAerCorsiaCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);
        when(aviationAerCorsiaCreationRequestParamsBuilderService.buildRequestParams(eq(accountId), any())).thenReturn(requestParams);

        aviationAerCreationService.createRequestAviationAer(accountId);

        verify(aviationAerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(aviationAerCreationValidatorService, times(1)).validateReportingYear(eq(accountId), any());
        verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(aviationAerCorsiaCreationRequestParamsBuilderService, times(1)).buildRequestParams(eq(accountId), any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verifyNoInteractions(aviationAerReportingObligationService);
    }

    @Test
    void createRequestAviationAer_when_ukets_for_account_reporting_status_exempt() {
        Long accountId = 1L;
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountInfoDTO aviationAccountInfo = AviationAccountInfoDTO.builder()
            .emissionTradingScheme(emissionTradingScheme)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .build();
        RequestParams requestParams = RequestParams.builder().build();
        Request request = Request.builder().build();

        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        when(aviationAerCreationValidatorService.validateAccountStatus(accountId)).thenReturn(validationResult);
        when(aviationAerCreationValidatorService.validateReportingYear(eq(accountId), any())).thenReturn(validationResult);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(aviationAccountInfo);
        when(aviationAerUkEtsCreationRequestParamsBuilderService.getEmissionTradingScheme()).thenReturn(emissionTradingScheme);
        when(aviationAerUkEtsCreationRequestParamsBuilderService.buildRequestParams(eq(accountId), any())).thenReturn(requestParams);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        aviationAerCreationService.createRequestAviationAer(accountId);

        verify(aviationAerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(aviationAerCreationValidatorService, times(1)).validateReportingYear(eq(accountId), any());
        verify(aviationAccountQueryService, times(1)).getAviationAccountInfoDTOById(accountId);
        verify(aviationAerUkEtsCreationRequestParamsBuilderService, times(1)).buildRequestParams(eq(accountId), any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(aviationAerReportingObligationService, times(1)).markAsExempt(request, null);
    }

    @Test
    void createRequestAviationAer_when_invalid_account_status_throw_error() {
        Long accountId = 1L;

        RequestCreateValidationResult invalidResult = RequestCreateValidationResult.builder().valid(false).build();

        when(aviationAerCreationValidatorService.validateAccountStatus(accountId)).thenReturn(invalidResult);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> aviationAerCreationService.createRequestAviationAer(accountId));

        assertEquals(MetsErrorCode.AVIATION_AER_CREATION_NOT_ALLOWED_INVALID_ACCOUNT_STATUS, businessException.getErrorCode());

        verify(aviationAerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verifyNoMoreInteractions(aviationAerCreationValidatorService);
        verifyNoInteractions(aviationAccountQueryService, aviationAerUkEtsCreationRequestParamsBuilderService, startProcessRequestService);
    }

    @Test
    void createRequestAviationAer_when_already_exists_for_year_throw_error() {
        Long accountId = 1L;

        RequestCreateValidationResult validResult = RequestCreateValidationResult.builder().valid(true).build();
        RequestCreateValidationResult invalidResult = RequestCreateValidationResult.builder().valid(false).build();

        when(aviationAerCreationValidatorService.validateAccountStatus(accountId)).thenReturn(validResult);
        when(aviationAerCreationValidatorService.validateReportingYear(eq(accountId), any())).thenReturn(invalidResult);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> aviationAerCreationService.createRequestAviationAer(accountId));

        assertEquals(MetsErrorCode.AVIATION_AER_ALREADY_EXISTS_FOR_REPORTING_YEAR, businessException.getErrorCode());

        verify(aviationAerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(aviationAerCreationValidatorService, times(1)).validateReportingYear(eq(accountId), any());
        verifyNoInteractions(aviationAccountQueryService, aviationAerUkEtsCreationRequestParamsBuilderService, startProcessRequestService);
    }
}