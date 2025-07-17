package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaEmissions;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<AviationDoECorsiaRequestPayload> {

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;
    private final RequestService requestService;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AVIATION_DOE_SUBMIT;
    }

    @Override
    public Map<String, Object> constructParams(AviationDoECorsiaRequestPayload payload, String requestId) {

        AviationDoECorsia doe = payload.getDoe();
        Year reportingYear = payload.getReportingYear();

        Request request = requestService.findRequestById(requestId);

        Optional<AviationReportableEmissionsEntity> reportableEmissions =
                 aviationReportableEmissionsRepository.findByAccountIdAndYear(request.getAccountId(), reportingYear);

        if (reportableEmissions.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        AviationDoECorsiaEmissions emissions = AviationDoECorsiaEmissions
                .builder()
                .emissionsAllInternationalFlights(reportableEmissions.get().getReportableEmissions())
                .emissionsFlightsWithOffsettingRequirements(reportableEmissions.get().getReportableOffsetEmissions())
                .emissionsClaimFromCorsiaEligibleFuels(reportableEmissions.get().getReportableReductionClaimEmissions())
                .calculationApproach(doe.getEmissions().getCalculationApproach())
                .build();

        return constructParams(doe, emissions, reportingYear);
    }

    public Map<String, Object> constructParams(AviationDoECorsiaApplicationSubmitRequestTaskPayload payload,
                                               Year reportingYear,
                                               Long accountId) {

        AviationDoECorsia doe = payload.getDoe();

        Optional<AviationReportableEmissionsEntity> reportableEmissions =
                 aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, reportingYear);


        AviationDoECorsiaEmissions emissions = applyDOECorrectionsToReportableEmissions(doe, reportableEmissions);

        return constructParams(doe, emissions, reportingYear);
    }

    private Map<String, Object> constructParams(AviationDoECorsia doe,
                                                AviationDoECorsiaEmissions emissions,
                                                Year reportingYear) {


        Map<String, Object> vars = new HashMap<>();

        vars.put("reportingYear", reportingYear);
        vars.put("InternationalEmissions", emissions.getEmissionsAllInternationalFlights());
        vars.put("offsettingEmissions", emissions.getEmissionsFlightsWithOffsettingRequirements());
        vars.put("CEFEmissions",emissions.getEmissionsClaimFromCorsiaEligibleFuels());
        vars.put("determinationReason", doe.getDeterminationReason().getType().toString());
        vars.put("calculationApproach", emissions.getCalculationApproach());

        return vars;
    }

    private AviationDoECorsiaEmissions applyDOECorrectionsToReportableEmissions(AviationDoECorsia doe,
                                                              Optional<AviationReportableEmissionsEntity> reportableEmissions) {


        AviationDoECorsiaEmissions emissions = doe.getEmissions();

        if (reportableEmissions.isPresent()) {

            if (emissions.getEmissionsAllInternationalFlights() == null &&
                    reportableEmissions.get().getReportableEmissions() != null) {
                emissions.setEmissionsAllInternationalFlights(reportableEmissions.get().getReportableEmissions());
            }

            if (emissions.getEmissionsFlightsWithOffsettingRequirements() == null &&
                    reportableEmissions.get().getReportableOffsetEmissions() != null) {
                 emissions.setEmissionsFlightsWithOffsettingRequirements(reportableEmissions.get().getReportableOffsetEmissions());
            }

            if (emissions.getEmissionsClaimFromCorsiaEligibleFuels() == null &&
                    reportableEmissions.get().getReportableReductionClaimEmissions() != null) {
                 emissions.setEmissionsClaimFromCorsiaEligibleFuels(reportableEmissions.get().getReportableReductionClaimEmissions());
            }
        }

        return emissions;
    }

}

