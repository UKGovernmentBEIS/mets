package uk.gov.pmrv.api.workflow.request.core.domain;

/**
 * Interface used to handle parallel requests' closing in case there are linked requests.
 */
public interface RequestPayloadCascadable {
    
    String getRelatedRequestId();
}
