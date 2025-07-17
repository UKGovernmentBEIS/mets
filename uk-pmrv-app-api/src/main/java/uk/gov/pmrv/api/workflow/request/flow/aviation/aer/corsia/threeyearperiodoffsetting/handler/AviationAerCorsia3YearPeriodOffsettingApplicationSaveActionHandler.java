package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class AviationAerCorsia3YearPeriodOffsettingApplicationSaveActionHandler
        implements RequestTaskActionHandler<AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload> {

    private final AviationAerCorsia3YearPeriodOffsettingSubmitService submitService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SAVE_APPLICATION);
    }
}
