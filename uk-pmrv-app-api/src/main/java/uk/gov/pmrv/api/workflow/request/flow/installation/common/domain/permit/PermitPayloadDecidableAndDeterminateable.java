package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

public interface PermitPayloadDecidableAndDeterminateable<T extends PermitReviewDecision, S extends Determinateable>
		extends PermitPayloadGroupDecidable<T>, PermitPayloadDeterminateable<S> {

}
