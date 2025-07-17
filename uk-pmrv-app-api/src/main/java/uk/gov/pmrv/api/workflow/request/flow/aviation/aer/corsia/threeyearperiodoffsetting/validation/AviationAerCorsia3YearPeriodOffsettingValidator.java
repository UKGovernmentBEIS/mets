package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingCalculationsService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Service
@RequiredArgsConstructor
@Validated
public class AviationAerCorsia3YearPeriodOffsettingValidator {

    private final AviationAerCorsia3YearPeriodOffsettingCalculationsService calculationsService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validate3YearPeriodOffsetting(@NotNull @Valid AviationAerCorsia3YearPeriodOffsetting
                                                      aviationAerCorsia3YearPeriodOffsetting) {

        Long totalCalculatedAnnualOffsetting =
                calculationsService
                        .calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(
                                aviationAerCorsia3YearPeriodOffsetting.getYearlyOffsettingData());
        Long totalCefEmissionsReductions =
                calculationsService
                        .calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(
                                aviationAerCorsia3YearPeriodOffsetting.getYearlyOffsettingData());

        Long periodOffsettingRequirements = calculationsService
                .calculatePeriodOffsettingRequirements(totalCalculatedAnnualOffsetting, totalCefEmissionsReductions);



        if (!totalCalculatedAnnualOffsetting.equals(
                aviationAerCorsia3YearPeriodOffsetting
                        .getTotalYearlyOffsettingData().getCalculatedAnnualOffsetting())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        if (!totalCefEmissionsReductions.equals(
                aviationAerCorsia3YearPeriodOffsetting
                        .getTotalYearlyOffsettingData().getCefEmissionsReductions())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        if (!periodOffsettingRequirements.equals(
                aviationAerCorsia3YearPeriodOffsetting.getPeriodOffsettingRequirements())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

    }

    public void validatePeerReviewRequest(final RequestTask requestTask,
                                          final PeerReviewRequestTaskActionPayload taskActionPayload,
                                          final AppUser appUser) {
        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(requestTask.getType(), taskActionPayload.getPeerReviewer(), appUser);

        validate3YearPeriodOffsetting(taskPayload.getAviationAerCorsia3YearPeriodOffsetting());

    }
}
