package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmitParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.mapper.AviationAerUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsCompleteServiceTest {

    @InjectMocks
    private AviationAerUkEtsCompleteService completeService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    private AviationAerService aviationAerService;

    @Mock
    private AviationAerUkEtsMapper aviationAerMapper;

    @Test
    void complete_when_reporting_required() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Long vbId = 2L;
        String operatorName = "operator name";
        final LimitedCompanyOrganisation organisation = LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .differentContactLocationExist(false)
                .organisationLocation(LocationOnShoreStateDTO.builder()
                        .type(LocationType.ONSHORE_STATE)
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .state("state")
                        .postcode("postcode")
                        .build())
                .build();
        AviationOperatorDetails operatorDetails  = AviationOperatorDetails.builder()
                .operatorName(operatorName)
                .organisationStructure(organisation)
                .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
                .operatorDetails(operatorDetails)
                .build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder().build())
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();
        AviationAerRequestMetadata metadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .verificationBodyId(vbId)
            .payload(requestPayload)
            .metadata(metadata)
            .build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(ServiceContactDetails.builder().build())
            .build();

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder().reportingRequired(true).aer(aer).build();

        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();
        AviationAerUkEtsTotalReportableEmissions reportableEmissions = AviationAerUkEtsTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(3456.10))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerUkEtsContainer(requestPayload, EmissionTradingScheme.UK_ETS_AVIATION, accountInfo, metadata))
            .thenReturn(aerContainer);
        when(aviationAerService.submitAer(submitAerParams)).thenReturn(Optional.of(reportableEmissions));

        //invoke
        completeService.complete(requestId);

        //verify
        AviationAerRequestMetadata updatedRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();

        assertEquals(reportableEmissions.getReportableEmissions(), updatedRequestMetadata.getEmissions());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestVerificationService, times(1)).refreshVerificationReportVBDetails(verificationReport, vbId);
        verify(aviationAerMapper, times(1)).toAviationAerUkEtsContainer(requestPayload, EmissionTradingScheme.UK_ETS_AVIATION, accountInfo, metadata);
        verify(aviationAerService, times(1)).submitAer(submitAerParams);
    }

    @Test
    void complete_when_reporting_not_required() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .reportingRequired(false)
            .build();
        AviationAerRequestMetadata metadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(requestPayload)
            .metadata(metadata)
            .build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(ServiceContactDetails.builder().build())
            .build();

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder().reportingRequired(false).build();

        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerUkEtsContainer(requestPayload, EmissionTradingScheme.UK_ETS_AVIATION, accountInfo, metadata))
            .thenReturn(aerContainer);
        when(aviationAerService.submitAer(submitAerParams)).thenReturn(Optional.empty());

        //invoke
        completeService.complete(requestId);

        //verify
        assertNull(metadata.getEmissions());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerMapper, times(1)).toAviationAerUkEtsContainer(requestPayload, EmissionTradingScheme.UK_ETS_AVIATION, accountInfo, metadata);
        verify(aviationAerService, times(1)).submitAer(submitAerParams);
    }

    @Test
    void addRequestAction() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Long vbId = 2L;
        String regulatorReviewer = "regulatorReviewer";
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder().build())
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .regulatorReviewer(regulatorReviewer)
            .build();
        AviationAerRequestMetadata metadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .verificationBodyId(vbId)
            .payload(requestPayload)
            .metadata(metadata)
            .build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(ServiceContactDetails.builder().build())
            .build();

        AviationAerUkEtsApplicationCompletedRequestActionPayload requestActionPayload =
            AviationAerUkEtsApplicationCompletedRequestActionPayload.builder().reportingRequired(true).aer(aer).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerUkEtsApplicationCompletedRequestActionPayload(requestPayload, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD, accountInfo, metadata))
            .thenReturn(requestActionPayload);

        //invoke
        completeService.addRequestAction(requestId, false);

        //verify
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_COMPLETED, regulatorReviewer);

    }
}