package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AviationCorsiaAer3YearPeriodOffsettingCreateValidatorTest {

    @InjectMocks
    private AviationCorsiaAer3YearPeriodOffsettingCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private ConfigurationService configurationService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new AviationCorsiaAer3YearPeriodOffsettingCreateValidator(requestCreateValidatorService, requestQueryService, configurationService);
    }

    @Test
    void testGetApplicableAccountStatuses() {
        Set<AccountStatus> statuses = validator.getApplicableAccountStatuses();
        assertThat(statuses).containsExactlyInAnyOrder(
                AviationAccountStatus.NEW,
                AviationAccountStatus.LIVE
        );
    }

    @Test
    void testGetReferableRequestType() {
        RequestType requestType = validator.getReferableRequestType();
        assertThat(requestType).isEqualTo(RequestType.AVIATION_AER_CORSIA);
    }

    @Test
    void testGetType() {
        RequestCreateActionType actionType = validator.getType();
        assertThat(actionType).isEqualTo(RequestCreateActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING);
    }

    @Test
    void testValidateRequestTypeWhenAnnualOffsettingInProgress() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2024);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);



        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(true);

        // Act
        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);

        // Assert
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING);
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void testValidateRequestTypeWhenNo3ΥearPeriodOffsettingInProgress() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2024);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);
        ConfigurationDTO configDtoMock = mock(ConfigurationDTO.class);
        when(configurationService.getConfigurationByKey("aer.corsia.3year.first-reporting-year")).thenReturn(Optional.of(configDtoMock));
        when(configDtoMock.getValue()).thenReturn(2024);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void test3YPOValidateRequestTypeWhenYearIsFirstReportingYear() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2026);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);
        ConfigurationDTO configDtoMock = mock(ConfigurationDTO.class);
        when(configurationService.getConfigurationByKey("aer.corsia.3year.first-reporting-year")).thenReturn(Optional.of(configDtoMock));
        when(configDtoMock.getValue()).thenReturn(2026);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isTrue();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void test3YPOValidateRequestTypeWhenYearIsAfterFirstReportingYearAndIsValid() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2029);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);
        ConfigurationDTO configDtoMock = mock(ConfigurationDTO.class);
        when(configurationService.getConfigurationByKey("aer.corsia.3year.first-reporting-year")).thenReturn(Optional.of(configDtoMock));
        when(configDtoMock.getValue()).thenReturn(2026);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isTrue();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void test3YPOValidateRequestTypeWhenYearIsAfterFirstReportingYearAndIsNotValid() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2027);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);
        ConfigurationDTO configDtoMock = mock(ConfigurationDTO.class);
        when(configurationService.getConfigurationByKey("aer.corsia.3year.first-reporting-year")).thenReturn(Optional.of(configDtoMock));
        when(configDtoMock.getValue()).thenReturn(2026);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isFalse();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void test3YPOValidateRequestTypeWhenYearIsBeforeFirstReportingYear() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2023);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);
        ConfigurationDTO configDtoMock = mock(ConfigurationDTO.class);
        when(configurationService.getConfigurationByKey("aer.corsia.3year.first-reporting-year")).thenReturn(Optional.of(configDtoMock));
        when(configDtoMock.getValue()).thenReturn(2024);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isFalse();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }



    @Test
    void test3YPOValidate_validateAction_returnsValidResult() {
        // Arrange
        Long accountId = 1L;
        String requestId = "1";
        Year metadataYear = Year.of(2026);

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
                requestId,
                validator.getReferableRequestType(),
                RequestStatus.IN_PROGRESS,
                LocalDateTime.now(),
                requestMetadata
        );

        ReportRelatedRequestCreateActionPayload payload = new ReportRelatedRequestCreateActionPayload();
        payload.setRequestId(requestId);

        when(requestQueryService.findRequestDetailsById(payload.getRequestId())).thenReturn(requestDetailsDTO);
        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false); //

        RequestCreateAccountStatusValidationResult accountStatusValidationResult =
                new RequestCreateAccountStatusValidationResult(true, AviationAccountStatus.LIVE);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, validator.getApplicableAccountStatuses()))
                .thenReturn(accountStatusValidationResult);

        // Act
        RequestCreateValidationResult result = validator.validateAction(accountId, payload);

        // Assert
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void test3YPOValidate_validateAction_throwsBusinessException() {
        /// Arrange
        Long accountId = 1L;
        String requestId = "1";
        Year metadataYear = Year.of(2026);

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
                requestId,
                RequestType.PERMIT_BATCH_REISSUE,
                RequestStatus.IN_PROGRESS,
                LocalDateTime.now(),
                requestMetadata
        );

        ReportRelatedRequestCreateActionPayload payload = new ReportRelatedRequestCreateActionPayload();
        payload.setRequestId(requestId);

        when(requestQueryService.findRequestDetailsById(payload.getRequestId())).thenReturn(requestDetailsDTO);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> validator.validateAction(accountId, payload));
        assertThat(exception.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_AVIATION_AER);
    }

    @Test
    void test3YPOValidate_validateAction_whenAccountStatusIsInvalid_returnsInvalidResult() {
        // Arrange
        Long accountId = 1L;
        String requestId = "1";
        Year metadataYear = Year.of(2026);

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(
                requestId,
                validator.getReferableRequestType(),
                RequestStatus.IN_PROGRESS,
                LocalDateTime.now(),
                requestMetadata
        );

        ReportRelatedRequestCreateActionPayload payload = new ReportRelatedRequestCreateActionPayload();
        payload.setRequestId(requestId);

        when(requestQueryService.findRequestDetailsById(payload.getRequestId())).thenReturn(requestDetailsDTO);

        RequestCreateAccountStatusValidationResult accountStatusValidationResult =
                new RequestCreateAccountStatusValidationResult(false, AviationAccountStatus.CLOSED);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, validator.getApplicableAccountStatuses()))
                .thenReturn(accountStatusValidationResult);

        // Act
        RequestCreateValidationResult result = validator.validateAction(accountId, payload);

        // Assert
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedAccountStatus()).isEqualTo(AviationAccountStatus.CLOSED);
        verify(requestCreateValidatorService).validateAccountStatuses(accountId, validator.getApplicableAccountStatuses());
    }
}
