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
public class AviationAerUkEtsVerificationPerformedRequestTaskActionValidator extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(
            RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION,
            RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND
        );
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerUkEtsApplicationSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        AviationAerUkEts aviationAerUkEts = aviationAerUkEtsApplicationSubmitRequestTaskPayload.getAer();

        if (Boolean.TRUE.equals(aviationAerUkEtsApplicationSubmitRequestTaskPayload.getReportingRequired()) &&
            (aviationAerUkEts.getSaf().getExist() ||
                EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(aviationAerUkEts.getMonitoringApproach().getMonitoringApproachType()) ||
                EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS.equals(aviationAerUkEts.getMonitoringApproach().getMonitoringApproachType())
            ) && !aviationAerUkEtsApplicationSubmitRequestTaskPayload.isVerificationPerformed()) {
            return RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
        }

        return RequestTaskActionValidationResult.validResult();
    }
}
