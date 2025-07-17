package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BatchReissueMapper {

	@Mapping(target = "payloadType", source = "payloadType")
	BatchReissueSubmittedRequestActionPayload toSubmittedActionPayload(
			BatchReissueRequestPayload requestPayload, BatchReissueRequestMetadata metadata, String signatoryName, RequestActionPayloadType payloadType);
	
	@Mapping(target = "payloadType", source = "payloadType")
	@Mapping(target = "numberOfAccounts", expression = "java(metadata.getAccountsReports().size())")
	BatchReissueCompletedRequestActionPayload toCompletedActionPayload(
			BatchReissueRequestPayload requestPayload, BatchReissueRequestMetadata metadata, String signatoryName, RequestActionPayloadType payloadType);
}
