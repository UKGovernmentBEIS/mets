package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NonComplianceMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_APPLICATION_CLOSED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceApplicationClosedRequestActionPayload toClosedRequestAction(NonComplianceRequestPayload requestPayload);

    @AfterMapping
    default void setNonComplianceClosedAttachments(
        @MappingTarget NonComplianceApplicationClosedRequestActionPayload actionPayload,
        NonComplianceRequestPayload requestPayload) {
        actionPayload.setNonComplianceAttachments(requestPayload.getNonComplianceAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "selectedRequests", source = "taskPayload", qualifiedByName = "selectedRequests")
    NonComplianceApplicationSubmittedRequestActionPayload toSubmittedRequestAction(
        NonComplianceApplicationSubmitRequestTaskPayload taskPayload
    );

    @Named("selectedRequests")
    default Set<RequestInfoDTO> selectedRequests(NonComplianceApplicationSubmitRequestTaskPayload requestTaskPayload) {
        
        return requestTaskPayload.getSelectedRequests().stream().map(req ->
                new RequestInfoDTO(
                    req,
                    requestTaskPayload.getAvailableRequests()
                        .stream()
                        .filter(info -> info.getId().equals(req))
                        .findFirst()
                        .map(RequestInfoDTO::getType)
                        .orElse(null)
                )
            ).collect(Collectors.toSet());
    }
    
    @Mapping(target = "comments", ignore = true)
    NonComplianceApplicationSubmittedRequestActionPayload toSubmittedRequestActionIgnoreComments(
        NonComplianceApplicationSubmittedRequestActionPayload actionPayload
    );
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "decisionNotification", source = "decisionNotification")
    @Mapping(target = "usersInfo", source = "usersInfo")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload toDailyPenaltyNoticeSubmittedRequestAction(
        NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload,
        NonComplianceDecisionNotification decisionNotification,
        Map<String, RequestActionUserInfo> usersInfo);

    @AfterMapping
    default void setDailyPenaltyNoticeComplianceAttachments(
        @MappingTarget NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload actionPayload,
        NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload) {
        actionPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "comments", ignore = true)
    NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload toDailyPenaltyNoticeSubmittedRequestActionIgnoreComments(
        NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload actionPayload
    );

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "comments", source = "requestPayload.dailyPenaltyComments")
    NonComplianceDailyPenaltyNoticeRequestTaskPayload toNonComplianceDailyPenaltyNoticeRequestTaskPayload(
        NonComplianceRequestPayload requestPayload,
        RequestTaskPayloadType payloadType
    );
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "decisionNotification", source = "decisionNotification")
    @Mapping(target = "usersInfo", source = "usersInfo")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload toNoticeOfIntentSubmittedRequestAction(
        NonComplianceNoticeOfIntentRequestTaskPayload taskPayload,
        NonComplianceDecisionNotification decisionNotification,
        Map<String, RequestActionUserInfo> usersInfo);

    @AfterMapping
    default void setNoticeOfIntentNonComplianceAttachments(
        @MappingTarget NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload actionPayload,
        NonComplianceNoticeOfIntentRequestTaskPayload taskPayload) {
        actionPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
    }
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "comments", source = "requestPayload.noticeOfIntentComments")
    NonComplianceNoticeOfIntentRequestTaskPayload toNonComplianceNoticeOfIntentRequestTaskPayload(
        NonComplianceRequestPayload requestPayload,
        RequestTaskPayloadType payloadType
    );

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "comments", ignore = true)
    NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload toNoticeOfIntentSubmittedRequestActionIgnoreComments(
        NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload actionPayload
    );

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "penaltyAmount", source = "requestPayload.civilPenaltyAmount")
    @Mapping(target = "dueDate", source = "requestPayload.civilPenaltyDueDate")
    @Mapping(target = "comments", source = "requestPayload.civilPenaltyComments")
    NonComplianceCivilPenaltyRequestTaskPayload toNonComplianceCivilPenaltyRequestTaskPayload(
        NonComplianceRequestPayload requestPayload,
        RequestTaskPayloadType payloadType
    );
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "decisionNotification", source = "decisionNotification")
    @Mapping(target = "usersInfo", source = "usersInfo")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload toCivilPenaltySubmittedRequestAction(
        NonComplianceCivilPenaltyRequestTaskPayload taskPayload,
        NonComplianceDecisionNotification decisionNotification,
        Map<String, RequestActionUserInfo> usersInfo);

    @AfterMapping
    default void setCivilPenaltyNonComplianceAttachments(
        @MappingTarget NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload actionPayload,
        NonComplianceCivilPenaltyRequestTaskPayload taskPayload) {
        actionPayload.setNonComplianceAttachments(taskPayload.getNonComplianceAttachments());
    }
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "comments", ignore = true)
    NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload toCivilPenaltySubmittedRequestActionIgnoreComments(
        NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload actionPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload toFinalDeterminationSubmittedRequestAction(
        NonComplianceFinalDeterminationRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "finalDetermination.comments", ignore = true)
    NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload toFinalDeterminationSubmittedRequestActionIgnoreComments(
        NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload actionPayload);
}
