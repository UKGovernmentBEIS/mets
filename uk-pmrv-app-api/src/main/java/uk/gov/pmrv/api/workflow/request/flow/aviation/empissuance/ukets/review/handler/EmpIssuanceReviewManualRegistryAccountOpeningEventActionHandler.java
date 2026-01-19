package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryManualPushAvailabilityService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmpIssuanceReviewManualRegistryAccountOpeningEventActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationAccountRegistryManualPushAvailabilityService availabilityService;
    private final ApplicationEventPublisher publisher;


    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        String requestId = requestTask.getRequest().getId();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload)
                requestTask.getPayload();

        Boolean available = availabilityService.isManualPushAvailable(requestId);

        if(!available) {
            log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, requestTask.getRequest().getAccountId(),
                    NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                    "Cannot send created aviation account to ETS Registry because the registry id already exists");
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_CREATE_REGISTRY_ID_EXISTENCE,
                    requestTask.getRequest().getAccountId());
        }

        AviationAccountCreatedRegistryEvent aviationAccountCreatedRegistryEvent =
                AviationAccountCreatedRegistryEvent.builder().requestId(requestId)
                .accountId(requestTask.getRequest().getAccountId())
                .emissionsMonitoringPlan(requestTaskPayload.getEmissionsMonitoringPlan()).appUser(appUser).build();

        publisher.publishEvent(aviationAccountCreatedRegistryEvent);

    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_UKETS_MANUAL_ACCOUNT_OPENING_REGISTRY);
    }
}
