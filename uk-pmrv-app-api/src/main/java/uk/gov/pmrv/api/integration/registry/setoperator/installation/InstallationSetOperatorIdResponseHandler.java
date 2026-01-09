package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;
import uk.gov.pmrv.api.integration.registry.setoperator.common.NotifyErrorDTO;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdErrorNotifierService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdEventOutcomeService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.RegistryIntegrationEventError;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdEventOutcome;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdResponseEvent;

import java.util.List;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationSetOperatorIdResponseHandler {

    private final OperatorIdEventOutcomeService operatorIdEventOutcomeService;
    private final InstallationSetOperatorIdSendToRegistryProducer registryProducer;
    private final OperatorIdErrorNotifierService operatorIdErrorNotifierService;


    public void handleResponse(SetOperatorIdResponseEvent event , String correlationId) {
        try {
            SetOperatorIdEventOutcome eventOutcome = operatorIdEventOutcomeService.getOperatorIdEventOutcome(event);
            registryProducer.produce(eventOutcome);
            if (RegistryResponseStatus.ERROR.equals(eventOutcome.getOutcome())) {
                NotifyErrorDTO notifyErrorDTO = NotifyErrorDTO.builder()
                        .outcome(eventOutcome)
                        .correlationId(correlationId)
                        .event(event)
                        .service(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY)
                        .build();
                operatorIdErrorNotifierService.notifyAuthority(notifyErrorDTO);
                log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                        NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Unable to set the operator id from registry " + event);
            }
            else {
                log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                        NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Operator Id received from registry " + event);
            }

        }
        catch(Exception ex) {
            NotifyErrorDTO notifyErrorDTO = NotifyErrorDTO.builder()
                    .outcome(operatorIdEventOutcomeService.getInternalErrorEventOutcome(event))
                    .correlationId(correlationId)
                    .event(event)
                    .service(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY)
                    .build();
            registryProducer.produce(SetOperatorIdEventOutcome.builder().event(event)
                    .errors(List.of(RegistryIntegrationEventError.builder()
                            .error(RegistryResponseErrorCode.ERROR_0200)
                            .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription()).build())).build());
            log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                    NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Unable to set the operator id from registry " + event);
            operatorIdErrorNotifierService.notifyAuthority(notifyErrorDTO);

        }

    }

}
