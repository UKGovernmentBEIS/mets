package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpCorsiaReviewMapper {

    @Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.CORSIA)")
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceCorsiaApplicationReviewRequestTaskPayload toEmpIssuanceCorsiaApplicationReviewRequestTaskPayload(
        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload, RequestAviationAccountInfo aviationAccountInfo, RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    EmpIssuanceCorsiaApplicationApprovedRequestActionPayload toEmpIssuanceCorsiaApplicationApprovedRequestActionPayload(
        EmpIssuanceCorsiaRequestPayload requestPayload, RequestAviationAccountInfo accountInfo,
        Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload toEmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload(
        EmpIssuanceCorsiaRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "reviewGroupDecisions", source = "empIssuanceCorsiaRequestPayload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload toEmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload(
        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload, RequestAviationAccountInfo aviationAccountInfo,
        RequestTaskPayloadType payloadType);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
                                           EmpIssuanceCorsiaRequestPayload payload) {
        Set<UUID> amendFiles = requestTaskPayload.getReviewGroupDecisions().values().stream()
            .filter(empReviewDecision -> empReviewDecision.getType() == EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                empReviewDecision -> ((ChangesRequiredDecisionDetails) empReviewDecision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

        Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
            .filter(entry -> amendFiles.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestTaskPayload.setReviewAttachments(reviewFiles);
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload toEmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload(
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload payload,
        RequestActionPayloadType payloadType);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload actionPayload,
                                           EmpIssuanceCorsiaApplicationReviewRequestTaskPayload payload) {
        Set<UUID> amendFiles = actionPayload.getReviewGroupDecisions().values().stream()
            .filter(empReviewDecision -> empReviewDecision.getType() == EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                empReviewDecision -> ((ChangesRequiredDecisionDetails) empReviewDecision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

        Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
            .filter(entry -> amendFiles.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        actionPayload.setReviewAttachments(reviewFiles);
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload toEmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload(EmpIssuanceCorsiaRequestPayload requestPayload,
                                                                                                                                     RequestAviationAccountInfo accountInfo);
    @AfterMapping
    default void setOperatorDetailsName(@MappingTarget EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                                   RequestAviationAccountInfo accountInfo) {
        EmpCorsiaOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setOperatorName(accountInfo.getOperatorName());
    }

    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                   EmpIssuanceCorsiaRequestPayload requestPayload) {
        requestActionPayload.setEmpAttachments(requestPayload.getEmpAttachments());
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecision) {
        return reviewGroupDecision.entrySet().stream()
            .filter(entry -> entry.getValue().getType().equals(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                    EmpIssuanceReviewDecision.builder()
                        .type(entry.getValue().getType())
                        .details(ChangesRequiredDecisionDetails.builder()
                            .requiredChanges(((ChangesRequiredDecisionDetails) entry.getValue().getDetails()).getRequiredChanges()).build())
                        .build())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
