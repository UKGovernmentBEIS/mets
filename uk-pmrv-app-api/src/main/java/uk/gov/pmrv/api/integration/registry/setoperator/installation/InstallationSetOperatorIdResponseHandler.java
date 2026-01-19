package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.NotifyErrorDTO;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdErrorNotifierService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdEventOutcomeService;

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
    private final AccountQueryService accountQueryService;


    public void handleResponse(OperatorUpdateEvent event , String correlationId) {
        try {
            OperatorUpdateEventOutcome eventOutcome = operatorIdEventOutcomeService.getInstallationOperatorIdEventOutcome(event);
            registryProducer.produce(eventOutcome);
            if (IntegrationEventOutcome.ERROR.equals(eventOutcome.getOutcome())) {
                NotifyErrorDTO notifyErrorDTO = NotifyErrorDTO.builder()
                        .outcome(eventOutcome)
                        .correlationId(correlationId)
                        .event(event)
                        .service(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY)
                        .build();
                setAuthorityAndName(event, notifyErrorDTO);
                operatorIdErrorNotifierService.notifyAuthority(notifyErrorDTO);
                log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                        NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Unable to set the operator id from registry " + event);
            }
        }
        catch(Exception ex) {
            NotifyErrorDTO notifyErrorDTO = NotifyErrorDTO.builder()
                    .outcome(operatorIdEventOutcomeService.getInternalErrorEventOutcome(event))
                    .correlationId(correlationId)
                    .event(event)
                    .service(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY)
                    .build();
            setAuthorityAndName(event, notifyErrorDTO);
            registryProducer.produce(OperatorUpdateEventOutcome.builder().event(event)
                    .errors(List.of(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0200)
                    .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription()).build())).build());
            log.error(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                    NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, String.format("Unable to set the operator id " +
                            "from registry [%s] with error : %s",event,ex.getMessage()));
            operatorIdErrorNotifierService.notifyAuthority(notifyErrorDTO);

        }

    }

    private void setAuthorityAndName(OperatorUpdateEvent event , NotifyErrorDTO notifyErrorDTO) {
        CompetentAuthorityEnum competentAuthority = NotifyRegistryUtils.toCompetentAuthorityFromCode(event.getRegulator());
        Account account = accountQueryService.getAccountByEmitterId(event.getEmitterId()).orElse(null);
        if(ObjectUtils.isEmpty(competentAuthority)) {
            competentAuthority = account==null ? null : account.getCompetentAuthority();
        }
        notifyErrorDTO.setAuthority(competentAuthority);
        notifyErrorDTO.setAccountName(account==null ? null : account.getName());

    }

}
