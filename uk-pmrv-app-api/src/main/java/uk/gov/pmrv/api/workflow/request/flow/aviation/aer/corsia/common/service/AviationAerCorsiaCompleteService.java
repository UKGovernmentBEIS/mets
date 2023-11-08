package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmitParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaCompleteService {

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService<AviationAerCorsiaVerificationReport> requestVerificationService;
    private final AviationAerService aviationAerService;
    private final AviationAerCorsiaMapper aviationAerMapper;
    private final AviationAccountUpdateService aviationAccountUpdateService;

    @Transactional
    public void complete(String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationAerCorsiaRequestPayload aerRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        AviationAerCorsiaRequestMetadata aerMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        //update verification body details if verification is performed
        AviationAerCorsiaVerificationReport verificationReport = aerRequestPayload.getVerificationReport();
        Optional.ofNullable(verificationReport).ifPresent(report ->
            verificationReport.setVerificationBodyDetails(requestVerificationService
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
        );

        AviationAerCorsiaContainer aerContainer =
            aviationAerMapper.toAviationAerCorsiaContainer(aerRequestPayload, EmissionTradingScheme.CORSIA, accountInfo, aerMetadata);

        //submit aer
        AviationAerSubmitParams submitAerParams = AviationAerSubmitParams.builder()
            .accountId(accountId)
            .aerContainer(aerContainer)
            .build();
        Optional<AviationAerTotalReportableEmissions> reportableEmissions = aviationAerService.submitAer(submitAerParams);

        //update metadata with reportable emissions
        reportableEmissions.ifPresent(emissions -> {
            AviationAerCorsiaTotalReportableEmissions corsiaEmissions =
                    (AviationAerCorsiaTotalReportableEmissions) emissions;

            aerMetadata.setEmissions(corsiaEmissions.getReportableEmissions());
            aerMetadata.setTotalEmissionsOffsettingFlights(corsiaEmissions.getReportableOffsetEmissions());
            aerMetadata.setTotalEmissionsClaimedReductions(corsiaEmissions.getReportableReductionClaimEmissions());
        });

        //update account upon AER workflow completion
        if(Boolean.TRUE.equals(aerContainer.getReportingRequired())) {
            AviationCorsiaOperatorDetails operatorDetails = aerContainer.getAer().getOperatorDetails();
            aviationAccountUpdateService.updateAccountUponAerCompletion(accountId,
                operatorDetails.getOperatorName(), getContactLocationFromAER(operatorDetails));
        }
    }

    public void addRequestAction(String requestId, boolean skipped) {
        Request request = requestService.findRequestById(requestId);
        AviationAerCorsiaRequestPayload aerRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        AviationAerCorsiaRequestMetadata aerMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        Long accountId = request.getAccountId();

        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(accountId);

        //update verification body details if verification is performed
        AviationAerCorsiaVerificationReport verificationReport = aerRequestPayload.getVerificationReport();
        Optional.ofNullable(verificationReport).ifPresent(report ->
            verificationReport.setVerificationBodyDetails(requestVerificationService
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
        );

        AviationAerCorsiaApplicationCompletedRequestActionPayload requestActionPayload =
            aviationAerMapper.toAviationAerCorsiaApplicationCompletedRequestActionPayload(
                aerRequestPayload, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD, accountInfo, aerMetadata);

        RequestActionType actionType = skipped ?
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED :
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED;
        // Add action completed
        requestService.addActionToRequest(request,
            requestActionPayload,
            actionType,
            aerRequestPayload.getRegulatorReviewer());
    }

    private LocationOnShoreStateDTO getContactLocationFromAER(AviationCorsiaOperatorDetails operatorDetails) {
        OrganisationStructure organisationStructure = operatorDetails.getOrganisationStructure();

        if (OrganisationLegalStatusType.LIMITED_COMPANY.equals(organisationStructure.getLegalStatusType())) {

            LimitedCompanyOrganisation limitedCompanyOrganisation = (LimitedCompanyOrganisation) organisationStructure;

            if (Boolean.TRUE.equals(limitedCompanyOrganisation.getDifferentContactLocationExist())) {
                return limitedCompanyOrganisation.getDifferentContactLocation();
            }
        }

        return organisationStructure.getOrganisationLocation();
    }
}
