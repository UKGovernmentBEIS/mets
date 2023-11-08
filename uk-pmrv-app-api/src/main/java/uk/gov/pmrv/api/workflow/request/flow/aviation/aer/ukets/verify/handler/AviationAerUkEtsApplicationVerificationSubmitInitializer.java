package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper.AviationAerUkEtsVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService<AviationAerUkEtsVerificationReport> requestVerificationService;
    private final AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;
    private static final AviationAerUkEtsVerifyMapper AVIATION_AER_UK_ETS_VERIFY_MAPPER = Mappers.getMapper(AviationAerUkEtsVerifyMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        AviationAerRequestMetadata aviationAerRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        AviationAerUkEtsRequestPayload aviationAerRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        AviationAerUkEts aer = aviationAerRequestPayload.getAer();
        Long requestVerificationBodyId = request.getVerificationBodyId();

        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        BigDecimal totalEmissions = aviationAerUkEtsEmissionsCalculationService.calculateTotalSubmittedEmissions(aer);

        // If VB id is changed clear verification report from request
        requestVerificationService.clearVerificationReport(aviationAerRequestPayload, requestVerificationBodyId);

        AviationAerUkEtsVerificationReport existingVerificationReport = aviationAerRequestPayload.getVerificationReport();

        AviationAerUkEtsVerificationReport newVerificationReport = AviationAerUkEtsVerificationReport.builder()
                .verificationBodyId(requestVerificationBodyId)
                .verificationBodyDetails(requestVerificationService
                        .getVerificationBodyDetails(existingVerificationReport, requestVerificationBodyId))
                .safExists(aer.getSaf().getExist())
                .verificationData(!ObjectUtils.isEmpty(existingVerificationReport)
                        ? existingVerificationReport.getVerificationData()
                        : initializeAviationAerVerificationData(aviationAerRequestPayload.getAer()))
                .build();

        return AVIATION_AER_UK_ETS_VERIFY_MAPPER.toAviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload(
                aviationAerRequestPayload, newVerificationReport, aviationAccountInfo, totalEmissions,
                aviationAerRequestMetadata.getYear(), RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(
            RequestTaskType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }

    private AviationAerUkEtsVerificationData initializeAviationAerVerificationData(AviationAerUkEts aer) {
        Set<AviationAerUkEtsFuelType> fuelTypesProvided = aer.getAggregatedEmissionsData().getAggregatedEmissionDataDetails().stream()
                .map(AviationAerUkEtsAggregatedEmissionDataDetails::getFuelType)
                .collect(Collectors.toSet());

        return AviationAerUkEtsVerificationData.builder()
                .opinionStatement(AviationAerOpinionStatement.builder().fuelTypes(fuelTypesProvided).build())
                .build();
    }
}
