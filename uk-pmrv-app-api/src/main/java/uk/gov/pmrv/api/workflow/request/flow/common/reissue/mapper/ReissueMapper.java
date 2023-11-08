package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ReissueMapper {

	@Mapping(target = "payloadType", source = "payloadType")
	ReissueCompletedRequestActionPayload toCompletedActionPayload(ReissueRequestPayload requestPayload,
			ReissueRequestMetadata requestMetadata, String signatoryName, RequestActionPayloadType payloadType);
	
}
