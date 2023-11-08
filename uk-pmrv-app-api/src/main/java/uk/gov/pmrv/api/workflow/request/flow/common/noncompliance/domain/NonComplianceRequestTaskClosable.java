package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

public interface NonComplianceRequestTaskClosable extends NonComplianceRequestTaskAttachable {
    
    void setCloseJustification(NonComplianceCloseJustification justification);
}
