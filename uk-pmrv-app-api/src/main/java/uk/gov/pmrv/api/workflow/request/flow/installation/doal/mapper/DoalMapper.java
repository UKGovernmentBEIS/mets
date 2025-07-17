package uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedWithCorrectionsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.WithholdingAllowancesNotice;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Mapping(target = "doal.additionalDocuments", ignore = true)
    @Mapping(target = "doal.operatorActivityLevelReport.comment", ignore = true)
    @Mapping(target = "doal.verificationReportOfTheActivityLevelReport.comment", ignore = true)
    @Mapping(target = "doal.activityLevelChangeInformation.commentsForUkEtsAuthority", ignore = true)
    @Mapping(target = "doal.activityLevelChangeInformation.activityLevels", source = "doal.activityLevelChangeInformation.activityLevels" , qualifiedByName = "cloneActivityLevelsWithoutComments")
    DoalApplicationClosedRequestActionPayload cloneRespondedPayloadIgnoreRegulatorsCommentsAndAdditionalDocuments(DoalApplicationClosedRequestActionPayload payload);


    @Mapping(target = "doal.determination", source = "doal.determination", qualifiedByName = "cloneProceedToAuthorityDeterminationWithoutComment")
    @Mapping(target = "doal.additionalDocuments", ignore = true)
    @Mapping(target = "doal.operatorActivityLevelReport.comment", ignore = true)
    @Mapping(target = "doal.verificationReportOfTheActivityLevelReport.comment", ignore = true)
    @Mapping(target = "doal.activityLevelChangeInformation.commentsForUkEtsAuthority", ignore = true)
    @Mapping(target = "doal.activityLevelChangeInformation.activityLevels", source = "doal.activityLevelChangeInformation.activityLevels" , qualifiedByName = "cloneActivityLevelsWithoutComments")
    DoalApplicationProceededToAuthorityRequestActionPayload cloneRespondedPayloadIgnoreRegulatorsCommentsAndAdditionalDocuments(DoalApplicationProceededToAuthorityRequestActionPayload payload);

    @Named("cloneProceedToAuthorityDeterminationWithoutComment")
    default DoalProceedToAuthorityDetermination cloneProceedToAuthorityDeterminationWithoutComment(DoalDetermination determination) {
        DoalProceedToAuthorityDetermination proceedToAuthorityDetermination = (DoalProceedToAuthorityDetermination) determination;
        return DoalProceedToAuthorityDetermination.builder().type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                .reason(proceedToAuthorityDetermination.getReason()).articleReasonGroupType(proceedToAuthorityDetermination.getArticleReasonGroupType())
                .articleReasonItems(new HashSet<>(proceedToAuthorityDetermination.getArticleReasonItems()))
            .hasWithholdingOfAllowances(proceedToAuthorityDetermination.getHasWithholdingOfAllowances())
                .withholdingAllowancesNotice(WithholdingAllowancesNotice.builder()
                    .noticeIssuedDate(
                        Optional.ofNullable(proceedToAuthorityDetermination.getWithholdingAllowancesNotice())
                            .map(WithholdingAllowancesNotice::getNoticeIssuedDate).orElse(null))
                    .build())
                    .needsOfficialNotice(proceedToAuthorityDetermination.getNeedsOfficialNotice()).build();
    }

    @Named("cloneActivityLevelsWithoutComments")
    default List<ActivityLevel> cloneActivityLevelsWithoutComments(List<ActivityLevel> activityLevels) {
        return activityLevels.stream().map(item -> ActivityLevel.builder().year(item.getYear())
                .subInstallationName(item.getSubInstallationName()).changeType(item.getChangeType())
                .otherChangeTypeName(item.getOtherChangeTypeName()).changedActivityLevel(item.getChangedActivityLevel())
                .build()).collect(Collectors.toList());
    }

}
