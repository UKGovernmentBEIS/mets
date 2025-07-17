package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

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
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaimDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.mapper.AviationAerCorsiaMapper;
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
class AviationAerCorsiaCompleteServiceTest {

    @InjectMocks
    private AviationAerCorsiaCompleteService completeService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    private AviationAerService aviationAerService;

    @Mock
    private AviationAerCorsiaMapper aviationAerMapper;

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
        AviationCorsiaOperatorDetails operatorDetails  = AviationCorsiaOperatorDetails.builder()
                .operatorName(operatorName)
                .organisationStructure(organisation)
                .build();
        AviationAerCorsia aer = AviationAerCorsia.builder()
                .operatorDetails(operatorDetails)
                .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                    .emissionsReductionClaimDetails(AviationAerCorsiaEmissionsReductionClaimDetails.builder()
                        .totalEmissions(BigDecimal.valueOf(1234.56))
                        .build())
                    .build()
                )
                .build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder().build())
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();
        AviationAerCorsiaRequestMetadata metadata = AviationAerCorsiaRequestMetadata.builder().build();
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

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder().reportingRequired(true).aer(aer).build();

        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();
        AviationAerCorsiaTotalReportableEmissions reportableEmissions = AviationAerCorsiaTotalReportableEmissions.builder()
            .reportableEmissions(BigDecimal.valueOf(3456.10))
            .reportableOffsetEmissions(BigDecimal.valueOf(3456.10))
            .reportableReductionClaimEmissions(BigDecimal.ZERO)
            .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerCorsiaContainer(requestPayload, EmissionTradingScheme.CORSIA, accountInfo, metadata))
            .thenReturn(aerContainer);
        when(aviationAerService.submitAer(submitAerParams)).thenReturn(Optional.of(reportableEmissions));

        //invoke
        completeService.complete(requestId);

        //verify
        AviationAerCorsiaRequestMetadata updatedRequestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();

        assertEquals(reportableEmissions.getReportableEmissions(), updatedRequestMetadata.getEmissions());
        assertEquals(reportableEmissions.getReportableOffsetEmissions(), updatedRequestMetadata.getTotalEmissionsOffsettingFlights());
        assertEquals(reportableEmissions.getReportableReductionClaimEmissions(), updatedRequestMetadata.getTotalEmissionsClaimedReductions());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(requestVerificationService, times(1)).refreshVerificationReportVBDetails(verificationReport, vbId);
        verify(aviationAerMapper, times(1)).toAviationAerCorsiaContainer(requestPayload, EmissionTradingScheme.CORSIA, accountInfo, metadata);
        verify(aviationAerService, times(1)).submitAer(submitAerParams);
    }

    @Test
    void complete_when_reporting_not_required() {
    	Long vbId = 2L;
        String requestId = "REQ_ID";
        Long accountId = 1L;
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder().build())
                .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(false)
            .verificationReport(verificationReport)
            .build();
        AviationAerCorsiaRequestMetadata metadata = AviationAerCorsiaRequestMetadata.builder().build();
        Request request = Request.builder()
            .id(requestId)
            .verificationBodyId(vbId)
            .accountId(accountId)
            .payload(requestPayload)
            .metadata(metadata)
            .build();

        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(ServiceContactDetails.builder().build())
            .build();

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder().reportingRequired(false).build();

        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerCorsiaContainer(requestPayload, EmissionTradingScheme.CORSIA, accountInfo, metadata))
            .thenReturn(aerContainer);
        when(aviationAerService.submitAer(submitAerParams)).thenReturn(Optional.empty());

        //invoke
        completeService.complete(requestId);

        //verify
        assertNull(metadata.getEmissions());
        assertNull(metadata.getTotalEmissionsClaimedReductions());
        assertNull(metadata.getTotalEmissionsOffsettingFlights());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerMapper, times(1)).toAviationAerCorsiaContainer(requestPayload, EmissionTradingScheme.CORSIA, accountInfo, metadata);
        verify(aviationAerService, times(1)).submitAer(submitAerParams);
        verify(requestVerificationService, times(1)).refreshVerificationReportVBDetails(verificationReport, vbId);
    }

    @Test
    void addRequestAction() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Long vbId = 2L;
        String regulatorReviewer = "regulatorReviewer";
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder().build())
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .regulatorReviewer(regulatorReviewer)
            .build();
        AviationAerCorsiaRequestMetadata metadata = AviationAerCorsiaRequestMetadata.builder().build();
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

        AviationAerCorsiaApplicationCompletedRequestActionPayload requestActionPayload =
            AviationAerCorsiaApplicationCompletedRequestActionPayload.builder().reportingRequired(true).aer(aer).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerMapper
            .toAviationAerCorsiaApplicationCompletedRequestActionPayload(requestPayload, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD, accountInfo, metadata))
            .thenReturn(requestActionPayload);

        //invoke
        completeService.addRequestAction(requestId, false);

        //verify
        verify(requestService, times(1))
            .addActionToRequest(request, requestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED, regulatorReviewer);

    }
}
