package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationReviewMapper {
    
    @Mapping(target = "payloadType", source = "payloadType")
    PermitVariationApplicationReviewRequestTaskPayload toPermitVariationApplicationReviewRequestTaskPayload(
        PermitVariationRequestPayload requestPayload, RequestTaskPayloadType payloadType);
    
    PermitContainer toPermitContainer(PermitVariationApplicationReviewRequestTaskPayload reviewRequestTaskPayload);
    
    @AfterMapping
	default void setActivationDate(@MappingTarget PermitContainer permitContainer,
			PermitVariationApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload) {
        if (applicationReviewRequestTaskPayload.getDetermination() != null &&
            DeterminationType.GRANTED == applicationReviewRequestTaskPayload.getDetermination().getType()) {
            permitContainer.setActivationDate(((GrantDetermination) applicationReviewRequestTaskPayload.getDetermination()).getActivationDate());
        }
    }

    @AfterMapping
	default void setAnnualEmissionsTargets(@MappingTarget PermitContainer permitContainer,
			PermitVariationApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload) {
        if (applicationReviewRequestTaskPayload.getDetermination() != null &&
            DeterminationType.GRANTED == applicationReviewRequestTaskPayload.getDetermination().getType()) {
            permitContainer.setAnnualEmissionsTargets(
                ((GrantDetermination) applicationReviewRequestTaskPayload.getDetermination()).getAnnualEmissionsTargets());
        }
    }
    
}
