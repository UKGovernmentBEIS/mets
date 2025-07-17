package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.mapper.AviationAerCorsiaVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    private static final AviationAerCorsiaVerifyMapper MAPPER = Mappers.getMapper(AviationAerCorsiaVerifyMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAerCorsiaRequestMetadata requestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        final AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        final AviationAerCorsia aer = requestPayload.getAer();
        final AviationAerCorsiaTotalEmissions totalEmissions = aviationAerCorsiaEmissionsCalculationService
                .calculateTotalSubmittedEmissions(aer, requestMetadata.getYear());

        final Long requestVBId = request.getVerificationBodyId();
        final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
                .map(VerificationReport::getVerificationBodyId).orElse(null);

        // If VB id is changed clear verification report from request
        if(isVbChanged(requestVBId, verificationReportVBId)) {
            requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
        }

        final AviationAerCorsiaVerificationReport latestVerificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> {
        			throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId);
        		}))
                .verificationData(isVbChanged(requestVBId, verificationReportVBId)
                        ? initializeVerificationData(requestPayload.getAer())
                        : requestPayload.getVerificationData())
                .build();

        // if emissions reduction claim not exist, reset the respective verification
        // property in the request task (request payload will be updated as well due to
        // by-reference behavior)
        if (BooleanUtils.isFalse(requestPayload.getAer().getEmissionsReductionClaim().getExist()) &&
                latestVerificationReport.getVerificationData() != null
        ) {
            latestVerificationReport.getVerificationData().setEmissionsReductionClaimVerification(null);
        }

        final RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        return MAPPER
                .toAviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload(requestPayload,
                        latestVerificationReport, aviationAccountInfo, totalEmissions.getAllFlightsEmissions(),
                        totalEmissions.getOffsetFlightsEmissions(), requestMetadata.getYear(),
                        RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(
                RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT,
                RequestTaskType.AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }

    private AviationAerCorsiaVerificationData initializeVerificationData(AviationAerCorsia aer) {
        Set<AviationAerCorsiaFuelType> fuelTypesProvided = aer.getAggregatedEmissionsData().getAggregatedEmissionDataDetails().stream()
                .map(AviationAerCorsiaAggregatedEmissionDataDetails::getFuelType)
                .collect(Collectors.toSet());

        return AviationAerCorsiaVerificationData.builder()
                .opinionStatement(AviationAerCorsiaOpinionStatement.builder().fuelTypes(fuelTypesProvided).build())
                .build();
    }

    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
        return !requestVBId.equals(verificationReportVBId);
    }
}
