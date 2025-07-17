package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationDreUkEtsMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "dreAttachments", ignore = true)
    AviationDreApplicationSubmittedRequestActionPayload toSubmittedActionPayload(
        AviationDreUkEtsRequestPayload requestPayload,  Map<String, RequestActionUserInfo> usersInfo);

    @AfterMapping
    default void setDreAttachments(@MappingTarget AviationDreApplicationSubmittedRequestActionPayload actionPayload,
                                   AviationDreUkEtsRequestPayload requestPayload) {
        actionPayload.setDreAttachments(requestPayload.getDreAttachments());
    }

    @Mapping(target = "dre.determinationReason.furtherDetails", ignore = true)
    @Mapping(target = "dre.fee.feeDetails.comments", ignore = true)
    AviationDreApplicationSubmittedRequestActionPayload cloneSubmittedRequestActionPayloadIgnoreFurtherDetailsFeeComments(
        AviationDreApplicationSubmittedRequestActionPayload payload);

    @Mapping(target = "payloadType", source = "payloadType")
    AviationDreUkEtsApplicationSubmitRequestTaskPayload toAviationDreUkEtsApplicationSubmitRequestTaskPayload(
        AviationDreUkEtsRequestPayload requestPayload, RequestTaskPayloadType payloadType);
}