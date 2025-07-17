package uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper;

import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.Determinateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadDeterminateable;

public interface PermitDeterminableMapper {

    default <T extends Determinateable> void setActivationDate(@MappingTarget PermitContainer permitContainer, PermitPayloadDeterminateable<T> permitDeterminatablePayload) {
    	if(permitDeterminatablePayload.getDetermination() != null && 
    			DeterminationType.GRANTED == permitDeterminatablePayload.getDetermination().getType()) {
            permitContainer.setActivationDate(((GrantDetermination) permitDeterminatablePayload.getDetermination()).getActivationDate());
        }
    }

    default <T extends Determinateable> void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer, PermitPayloadDeterminateable<T> permitDeterminatablePayload) {
    	if(permitDeterminatablePayload.getDetermination() != null &&
    			DeterminationType.GRANTED == permitDeterminatablePayload.getDetermination().getType()) {
            permitContainer.setAnnualEmissionsTargets(((GrantDetermination) permitDeterminatablePayload.getDetermination()).getAnnualEmissionsTargets());
        }
    }
    
    
}
