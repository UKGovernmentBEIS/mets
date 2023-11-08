package uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedWithCorrectionsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface DoalMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    DoalApplicationSubmitRequestTaskPayload toDoalApplicationSubmitRequestTaskPayload(DoalRequestPayload requestPayload,
                                                                                      RequestTaskPayloadType payloadType,
                                                                                      List<HistoricalActivityLevel> historicalActivityLevels,
                                                                                      Set<PreliminaryAllocation> historicalPreliminaryAllocations);

    @Mapping(target = "payloadType", source = "payloadType")
    DoalApplicationSubmitRequestTaskPayload toDoalApplicationSubmitRequestTaskPayload(DoalRequestPayload requestPayload,
                                                                                      RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    DoalApplicationProceededToAuthorityRequestActionPayload toDoalApplicationProceededToAuthorityRequestActionPayload(DoalRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DOAL_APPLICATION_CLOSED_PAYLOAD)")
    DoalApplicationClosedRequestActionPayload toDoalApplicationClosedRequestActionPayload(DoalRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DOAL_APPLICATION_ACCEPTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    DoalApplicationAcceptedRequestActionPayload toDoalApplicationAcceptedRequestActionPayload(DoalRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    DoalApplicationAcceptedWithCorrectionsRequestActionPayload toDoalApplicationAcceptedWithCorrectionsRequestActionPayload(DoalRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.DOAL_APPLICATION_REJECTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    DoalApplicationRejectedRequestActionPayload toDoalApplicationRejectedRequestActionPayload(DoalRequestPayload requestPayload);

    default DoalAuthorityResponseSubmittedRequestActionPayload toDoalAuthorityResponseSubmittedRequestActionPayload(DoalRequestPayload requestPayload, RequestActionType actionType) {
        return switch (actionType) {
            case DOAL_APPLICATION_ACCEPTED -> toDoalApplicationAcceptedRequestActionPayload(requestPayload);
            case DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS -> toDoalApplicationAcceptedWithCorrectionsRequestActionPayload(requestPayload);
            case DOAL_APPLICATION_REJECTED -> toDoalApplicationRejectedRequestActionPayload(requestPayload);
            default -> null;
        };
    }
}
