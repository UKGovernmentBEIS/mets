package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BDRS2Mapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "bdrs2Attachments", ignore = true)
    BDRS2ApplicationSubmittedRequestActionPayload toBDRS2ApplicationSubmittedRequestActionPayload(
            BDRS2ApplicationSubmitRequestTaskPayload taskPayload,
            RequestActionPayloadType payloadType);
}
