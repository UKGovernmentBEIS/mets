package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.settings.service.SettingsFeeService;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApplyScheduledFeeUpdatesHandlerFlowable implements JavaDelegate {

    private final SettingsFeeService settingsFeeService;

    @Override
    public void execute(DelegateExecution execution) {
        try {
            settingsFeeService.applyScheduledFeeUpdates();
        } catch (Exception ex) {
            log.error("Applying scheduled fee updates failed with {}", ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
