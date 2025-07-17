package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpUkEtsReviewMapper {

    @Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.UK_ETS_AVIATION)")
    EmissionsMonitoringPlanUkEtsContainer toEmissionsMonitoringPlanUkEtsContainer(
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceUkEtsApplicationReviewRequestTaskPayload toEmpIssuanceUkEtsApplicationReviewRequestTaskPayload(
        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload, RequestAviationAccountInfo aviationAccountInfo, RequestTaskPayloadType payloadType);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload,
                                            RequestAviationAccountInfo aviationAccountInfo) {
        EmpOperatorDetails operatorDetails = requestTaskPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(aviationAccountInfo.getCrcoCode());
    }
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "reviewGroupDecisions", source = "empIssuanceUkEtsRequestPayload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload toEmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload(
        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload, RequestAviationAccountInfo aviationAccountInfo, 
        RequestTaskPayloadType payloadType);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
                                            RequestAviationAccountInfo aviationAccountInfo) {
        EmpOperatorDetails operatorDetails = requestTaskPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(aviationAccountInfo.getCrcoCode());
    }
    
    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
    		EmpIssuanceUkEtsRequestPayload payload) {
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
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    EmpIssuanceUkEtsApplicationApprovedRequestActionPayload toEmpIssuanceUkEtsApplicationApprovedRequestActionPayload(
        EmpIssuanceUkEtsRequestPayload requestPayload, RequestAviationAccountInfo accountInfo,
        Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload toEmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload(
        EmpIssuanceUkEtsRequestPayload requestPayload, Map<String, RequestActionUserInfo> usersInfo, RequestActionPayloadType payloadType);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmpIssuanceUkEtsApplicationApprovedRequestActionPayload approvedRequestActionPayload,
                                            RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = approvedRequestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload toEmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload(
    		EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload,
        RequestActionPayloadType payloadType);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload actionPayload,
    		EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload) {
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

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload toEmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload(EmpIssuanceUkEtsRequestPayload requestPayload,
                                                                                                                                    RequestAviationAccountInfo accountInfo);

    @AfterMapping
    default void setOperatorDetailsNameAndCrcoCode(@MappingTarget EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                                   RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setOperatorName(accountInfo.getOperatorName());
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }

    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                   EmpIssuanceUkEtsRequestPayload requestPayload) {
        requestActionPayload.setEmpAttachments(requestPayload.getEmpAttachments());
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecision) {
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
