package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class RequestExpirationVarsBuilder {
    
    private final RequestCalculateExpirationService requestCalculateExpirationService;
    
    public Map<String, Object> buildExpirationVars(RequestExpirationType requestExpirationType) {
        final Date requestExpirationDate = requestCalculateExpirationService.calculateExpirationDate();
        return this.buildExpirationVars(requestExpirationType, requestExpirationDate);
    }
    
    public Map<String, Object> buildExpirationVars(RequestExpirationType requestExpirationType, Date expirationDate) {
        Map<String, Object> expirationVars = new HashMap<>();
        expirationVars.put(requestExpirationType.getCode() + BpmnProcessConstants._EXPIRATION_DATE, expirationDate);
        expirationVars.put(requestExpirationType.getCode() + BpmnProcessConstants._FIRST_REMINDER_DATE, 
        		requestCalculateExpirationService.calculateFirstReminderDate(expirationDate));
        expirationVars.put(requestExpirationType.getCode() + BpmnProcessConstants._SECOND_REMINDER_DATE, 
        		requestCalculateExpirationService.calculateSecondReminderDate(expirationDate));
        return expirationVars;
    }

}
