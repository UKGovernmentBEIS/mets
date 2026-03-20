package uk.gov.pmrv.api.workflow.bpmn.handler.bdrs2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;

@Log4j2
@Service
@RequiredArgsConstructor
public class BDRS2InitializationHandler implements JavaDelegate {

    private final ConfigurationService configurationService;
    private static final String BDRS2_TRIGGER_DATE = "bdrs2.trigger.date";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing {} for process instance id: {}", getClass().getName(), execution.getProcessInstanceId());

        configurationService.getConfigurationByKey(BDRS2_TRIGGER_DATE)
            .map(ConfigurationDTO::getValue)
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(dateStr -> LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd")))
            .ifPresentOrElse(
                date -> {
                    execution.setVariable("triggerWorkflow", LocalDate.now().isEqual(date));
                },
                () -> {
                    execution.setVariable("triggerWorkflow", false);
                    log.error("{}: BDRS2 trigger date configuration is missing or invalid", getClass().getName());
                }
            );
        log.info("{}: variable 'triggerWorkflow' set to {}", getClass().getName(), execution.getVariable("triggerWorkflow"));

    }
}
