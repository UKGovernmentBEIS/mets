package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FlowableDateUtils {

    public Date now() {
        return new Date();
    }

    public boolean isDateInThePast(Date date) {
        return date.before(new Date());
    }
    
}
