package uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface DreMapper {

	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DRE_APPLICATION_SUBMITTED_PAYLOAD)")
	@Mapping(target = "dreAttachments", ignore = true)
	DreApplicationSubmittedRequestActionPayload toSubmittedActionPayload(DreRequestPayload requestPayload);

	@AfterMapping
	default void setDreAttachments(@MappingTarget DreApplicationSubmittedRequestActionPayload actionPayload, DreRequestPayload requestPayload) {
		actionPayload.setDreAttachments(requestPayload.getDreAttachments());
	}
	
	@Mapping(target = "dre.determinationReason", ignore = true)
	DreApplicationSubmittedRequestActionPayload cloneSubmittedRequestActionPayloadIgnoreDeterminationReason(
			DreApplicationSubmittedRequestActionPayload payload);
	
	@Mapping(target = "payloadType", source = "payloadType")
	DreApplicationSubmitRequestTaskPayload toDreApplicationSubmitRequestTaskPayload(
        DreRequestPayload requestPayload, RequestTaskPayloadType payloadType);
}
