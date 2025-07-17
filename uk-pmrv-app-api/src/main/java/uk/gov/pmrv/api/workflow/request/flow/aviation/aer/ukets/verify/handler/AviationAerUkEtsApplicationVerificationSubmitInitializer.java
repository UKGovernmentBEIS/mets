package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper.AviationAerUkEtsVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    private final AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;
    private static final AviationAerUkEtsVerifyMapper MAPPER = Mappers.getMapper(AviationAerUkEtsVerifyMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAerRequestMetadata requestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        final AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        final AviationAerUkEts aer = requestPayload.getAer();
        
        final Long requestVBId = request.getVerificationBodyId();
        final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
				.map(VerificationReport::getVerificationBodyId).orElse(null);

        final RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        final BigDecimal totalEmissions = aviationAerUkEtsEmissionsCalculationService.calculateTotalSubmittedEmissions(aer);

        // If VB id is changed clear verification report from request
 		if(isVbChanged(requestVBId, verificationReportVBId)) {
 			requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
 		}

        final AviationAerUkEtsVerificationReport latestVerificationReport = AviationAerUkEtsVerificationReport.builder()
        		.verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> {
        			throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId);
        		}))
                .safExists(aer.getSaf().getExist())
                .verificationData(isVbChanged(requestVBId, verificationReportVBId)
    					? initializeVerificationData(requestPayload.getAer())
    					: requestPayload.getVerificationData())
                .build();
        
		// if saf not exist, reset the respective verification property in the request
		// task (request payload will be updated as well due to by-reference behavior)
        if (BooleanUtils.isFalse(requestPayload.getAer().getSaf().getExist()) && 
        			latestVerificationReport.getVerificationData() != null 
				) {
        	latestVerificationReport.getVerificationData().setEmissionsReductionClaimVerification(null);
        	latestVerificationReport.setSafExists(false);
         }

        final AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload requestTaskPayload = MAPPER.toAviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload(
        		requestPayload, latestVerificationReport, aviationAccountInfo, totalEmissions,
                requestMetadata.getYear(), RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD
        );
        
        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(
            RequestTaskType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }

    private AviationAerUkEtsVerificationData initializeVerificationData(AviationAerUkEts aer) {
        Set<AviationAerUkEtsFuelType> fuelTypesProvided = aer.getAggregatedEmissionsData().getAggregatedEmissionDataDetails().stream()
                .map(AviationAerUkEtsAggregatedEmissionDataDetails::getFuelType)
                .collect(Collectors.toSet());

        return AviationAerUkEtsVerificationData.builder()
                .opinionStatement(AviationAerOpinionStatement.builder().fuelTypes(fuelTypesProvided).build())
                .build();
    }
    
    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
    	return !requestVBId.equals(verificationReportVBId);
    }
}
