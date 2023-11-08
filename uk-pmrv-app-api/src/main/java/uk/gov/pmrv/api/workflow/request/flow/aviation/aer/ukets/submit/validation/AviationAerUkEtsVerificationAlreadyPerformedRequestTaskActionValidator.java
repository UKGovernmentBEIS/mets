package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
public class AviationAerUkEtsVerificationAlreadyPerformedRequestTaskActionValidator extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION
        );
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    /**
     * As per https://pmo.trasys.be/jira/browse/PMRV-5440, which refers to installation AER, we do not want to
     * re-send the application to VERIFIER if the verification report is mandatory but no actual changes have been performed
     * in the application since the first occurence of the verification report.
     */
    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerUkEtsApplicationSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        AviationAerUkEts aviationAerUkEts = aviationAerUkEtsApplicationSubmitRequestTaskPayload.getAer();
        EmissionsMonitoringApproachType emissionsMonitoringApproachType = aviationAerUkEts.getMonitoringApproach().getMonitoringApproachType();

        if (Boolean.TRUE.equals(aviationAerUkEtsApplicationSubmitRequestTaskPayload.getReportingRequired()) &&
            (aviationAerUkEts.getSaf().getExist() ||
                EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(emissionsMonitoringApproachType) ||
                EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS.equals(emissionsMonitoringApproachType)
            ) && aviationAerUkEtsApplicationSubmitRequestTaskPayload.isVerificationPerformed()) {
            return RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
        }

        return RequestTaskActionValidationResult.validResult();
    }
}
