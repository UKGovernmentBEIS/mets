package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class AviationVirReviewSaveActionHandler 
    implements RequestTaskActionHandler<AviationVirSaveReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirReviewService virReviewService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final AviationVirSaveReviewRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        virReviewService.saveReview(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_VIR_SAVE_REVIEW);
    }
}
