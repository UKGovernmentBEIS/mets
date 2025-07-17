package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonSubType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;

import java.math.BigDecimal;

@Service
@Validated
public class AviationDoECorsiaValidationService {

    public void validateAviationDoECorsia(@NotNull @Valid AviationDoECorsia doe) {
        validateAviationDoECorsiaDeterminationReason(doe);
        validateAviationDoECorsiaEmissions(doe);
        validateAviationDoECorsiaFee(doe.getFee());
    }

    private void validateAviationDoECorsiaDeterminationReason(AviationDoECorsia doe) {
       if (doe.getDeterminationReason().getType().equals(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)) {
            if (doe.getDeterminationReason().getSubtypes().isEmpty()) {
                throw new BusinessException(ErrorCode.FORM_VALIDATION);
            }
        }
    }

    private void validateAviationDoECorsiaEmissions(AviationDoECorsia doe) {

        if (doe.getDeterminationReason()
                .getType()
                .equals(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)) {

            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsClaimFromCorsiaEligibleFuels(), true, true );
            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsFlightsWithOffsettingRequirements(), true, true );
            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsAllInternationalFlights(), true, false);

        } else if (doe.getDeterminationReason()
                .getType()
                .equals(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)) {

            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsClaimFromCorsiaEligibleFuels(),
                    doe.getDeterminationReason()
                    .getSubtypes()
                    .contains(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS),
                    true);

            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsFlightsWithOffsettingRequirements(),
                    doe.getDeterminationReason()
                    .getSubtypes()
                    .contains(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS),
                    true);

            validateAviationDoECorsiaEmissionsFigure(doe.getEmissions().getEmissionsAllInternationalFlights(),
                    doe.getDeterminationReason()
                    .getSubtypes()
                    .contains(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS) ,
                    false);
        }

        BigDecimal allIntl = doe.getEmissions().getEmissionsAllInternationalFlights();
        BigDecimal offsetting = doe.getEmissions().getEmissionsFlightsWithOffsettingRequirements();

        if (allIntl != null && offsetting != null &&
                allIntl.compareTo(BigDecimal.ZERO) != 0 &&
                offsetting.compareTo(BigDecimal.ZERO) != 0 &&
                allIntl.compareTo(offsetting) < 0) {

            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

    }

    private void validateAviationDoECorsiaEmissionsFigure(BigDecimal emissionsFigure,
                                                          boolean shouldEmissionsFigureExist,
                                                          boolean isZeroAllowed) {

        if (!shouldEmissionsFigureExist && emissionsFigure != null && emissionsFigure.compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }


        if (shouldEmissionsFigureExist && (emissionsFigure == null ||
                emissionsFigure.compareTo(BigDecimal.ZERO) < 0 ||
                (!isZeroAllowed && emissionsFigure.compareTo(BigDecimal.ZERO) == 0) ||
                emissionsFigure.stripTrailingZeros().scale() > 0)) {
                throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    private void validateAviationDoECorsiaFee(AviationDoECorsiaFee fee) {

        if (fee.isChargeOperator()) {
            if (fee.getFeeDetails() == null) {
                throw new BusinessException(ErrorCode.FORM_VALIDATION);
            }
        }

    }

}
