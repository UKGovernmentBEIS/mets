package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
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
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaApplicationVerificationSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService<AviationAerCorsiaVerificationReport> requestVerificationService;
    private final AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    private static final AviationAerCorsiaVerifyMapper AVIATION_AER_CORSIA_VERIFY_MAPPER = Mappers.getMapper(AviationAerCorsiaVerifyMapper.class);


    @Override
    public RequestTaskPayload initializePayload(Request request) {
        AviationAerCorsiaRequestMetadata aviationAerRequestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        AviationAerCorsiaRequestPayload aviationAerRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        Long requestVerificationBodyId = request.getVerificationBodyId();

        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        AviationAerCorsia aer = aviationAerRequestPayload.getAer();

        AviationAerCorsiaTotalEmissions totalEmissions = aviationAerCorsiaEmissionsCalculationService
            .calculateTotalSubmittedEmissions(aer);

        // If VB id is changed clear verification report from request
        requestVerificationService.clearVerificationReport(aviationAerRequestPayload, requestVerificationBodyId);

        AviationAerCorsiaVerificationReport existingVerificationReport = aviationAerRequestPayload.getVerificationReport();

        AviationAerCorsiaVerificationReport newVerificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationBodyId(requestVerificationBodyId)
            .verificationBodyDetails(requestVerificationService
                .getVerificationBodyDetails(existingVerificationReport, requestVerificationBodyId))
            .verificationData(!ObjectUtils.isEmpty(existingVerificationReport)
                ? existingVerificationReport.getVerificationData()
                : initializeAviationAerVerificationData(aviationAerRequestPayload.getAer()))
            .build();

        return AVIATION_AER_CORSIA_VERIFY_MAPPER.toAviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload(
            aviationAerRequestPayload, newVerificationReport, aviationAccountInfo, totalEmissions.getAllFlightsEmissions(), totalEmissions.getOffsetFlightsEmissions(),
            aviationAerRequestMetadata.getYear(), RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(
            RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }

    private AviationAerCorsiaVerificationData initializeAviationAerVerificationData(AviationAerCorsia aer) {
        Set<AviationAerCorsiaFuelType> fuelTypesProvided = aer.getAggregatedEmissionsData().getAggregatedEmissionDataDetails().stream()
            .map(AviationAerCorsiaAggregatedEmissionDataDetails::getFuelType)
            .collect(Collectors.toSet());

        return AviationAerCorsiaVerificationData.builder()
            .opinionStatement(AviationAerCorsiaOpinionStatement.builder().fuelTypes(fuelTypesProvided).build())
            .build();
    }
}
