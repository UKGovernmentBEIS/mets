package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

class AviationCorsiaAerAnnualOffsettingReInitiateValidatorTest {

    @InjectMocks
    private AviationCorsiaAerAnnualOffsettingReInitiateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private RequestQueryService requestQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new AviationCorsiaAerAnnualOffsettingReInitiateValidator(requestCreateValidatorService, requestQueryService);
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
        assertThat(actionType).isEqualTo(RequestCreateActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING);
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
                RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(true);

        // Act
        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);

        // Assert
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING);
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void testValidateRequestTypeWhenNoAnnualOffsettingInProgress() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2024);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        when(requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        )).thenReturn(false);

        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        verify(requestQueryService).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, RequestStatus.IN_PROGRESS, accountId, metadataYear
        );
    }

    @Test
    void testValidateRequestTypeWhenNoAnnualOffsettingIsBeforeBaseYear() {
        // Arrange
        Long accountId = 1L;
        Year metadataYear = Year.of(2022);
        final String requestId = "1";

        AviationAerCorsiaRequestMetadata requestMetadata = new AviationAerCorsiaRequestMetadata();
        requestMetadata.setYear(metadataYear);
        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), requestMetadata);


        RequestCreateRequestTypeValidationResult result = validator.validateRequestType(accountId, requestDetailsDTO);
        assertThat(result.isValid()).isFalse();
    }
}
