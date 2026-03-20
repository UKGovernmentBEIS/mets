package uk.gov.pmrv.api.workflow.bpmn.handler.bdrs2;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2InitializationHandlerTest {

    @InjectMocks
    private BDRS2InitializationHandler handler;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_whenTriggerDateIsToday_setsTriggerWorkflowTrue() throws Exception {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        ConfigurationDTO config = new ConfigurationDTO();
        config.setValue(today);

        when(configurationService.getConfigurationByKey("bdrs2.trigger.date"))
            .thenReturn(Optional.of(config));

        handler.execute(execution);

        verify(execution).setVariable("triggerWorkflow", true);
    }

    @Test
    void execute_whenTriggerDateIsNotToday_setsTriggerWorkflowFalse() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        ConfigurationDTO config = new ConfigurationDTO();
        config.setValue(tomorrow);

        when(configurationService.getConfigurationByKey("bdrs2.trigger.date"))
            .thenReturn(Optional.of(config));

        handler.execute(execution);

        verify(execution).setVariable("triggerWorkflow", false);
    }

    @Test
    void execute_whenConfigurationIsMissing_setsTriggerWorkflowFalseAndLogsError() throws Exception {
        when(configurationService.getConfigurationByKey("bdrs2.trigger.date"))
            .thenReturn(Optional.empty());

        handler.execute(execution);

        verify(execution).setVariable("triggerWorkflow", false);
    }
}
