package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import java.util.Map;
import java.util.UUID;

public interface NonComplianceRequestTaskAttachable {

    Map<UUID, String> getNonComplianceAttachments();
}
