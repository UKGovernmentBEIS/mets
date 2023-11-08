package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class RequestTypeDocumentTemplateInfoMapper {

    private static Map<RequestType, String> map = new HashMap<>();
    
    static {
        map.put(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_ISSUANCE.getDescription());
        map.put(RequestType.PERMIT_SURRENDER, "your application to surrender/rationalise your permit");
        map.put(RequestType.PERMIT_NOTIFICATION, "your AEM Permit Notification");
        map.put(RequestType.PERMIT_VARIATION, "your application for a permit variation");
        map.put(RequestType.EMP_ISSUANCE_UKETS, "your application for an Emissions Monitoring Plan");
        map.put(RequestType.EMP_VARIATION_UKETS, "your application for an Emissions Monitoring Plan variation");
        map.put(RequestType.EMP_ISSUANCE_CORSIA, "your application for an Emissions Monitoring Plan");
        map.put(RequestType.EMP_VARIATION_CORSIA, "your application for an Emissions Monitoring Plan variation");
    }
    
    public String getTemplateInfo(RequestType requestType) {
        return map.getOrDefault(requestType, "N/A");
    }
}
