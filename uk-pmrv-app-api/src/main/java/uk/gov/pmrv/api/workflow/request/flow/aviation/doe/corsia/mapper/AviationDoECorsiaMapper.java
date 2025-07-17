package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationDoECorsiaMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_DOE_CORSIA_SUBMITTED_PAYLOAD)")
    @Mapping(target = "doeAttachments", ignore = true)
    AviationDoECorsiaSubmittedRequestActionPayload toSubmittedActionPayload(
            AviationDoECorsiaRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo);

    @AfterMapping
    default void setDoeAttachments(@MappingTarget AviationDoECorsiaSubmittedRequestActionPayload actionPayload,
                                   AviationDoECorsiaRequestPayload requestPayload) {
        actionPayload.setDoeAttachments(requestPayload.getDoeAttachments());
    }


    @Mapping(target = "payloadType", source = "payloadType")
    AviationDoECorsiaApplicationSubmitRequestTaskPayload toAviationDoECorsiaApplicationSubmitRequestTaskPayload(
            AviationDoECorsiaRequestPayload requestPayload, RequestTaskPayloadType payloadType);
}
