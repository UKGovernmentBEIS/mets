package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationCorsiaSubmitRegulatorLedMapper extends EmpVariationCorsiaOperatorDetailsMapper {
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisionsRegulatorLed")
    @Mapping(target = "reasonRegulatorLed", source = "requestPayload.reasonRegulatorLed")
    EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload 
    toEmpVariationApplicationRegulatorLedApprovedRequestActionPayload(
        EmpVariationCorsiaRequestPayload requestPayload, 
        RequestAviationAccountInfo accountInfo,
        Map<String, RequestActionUserInfo> usersInfo
    );

    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsWithoutNotes")
    EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload cloneRegulatorLedApprovedPayloadIgnoreDecisionNotes(
        EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload payload);

    @Named("reviewGroupDecisionsWithoutNotes")
    default Map<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> setReviewGroupDecisionsWithoutNotes(
        Map<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisions
    ) {
        return reviewGroupDecisions.entrySet().stream()
            .map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                    EmpAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(entry.getValue().getVariationScheduleItems())
                        .build())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.notEmptyDynamicSections", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisionsRegulatorLed")
    EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload
    toEmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload(
        EmpVariationCorsiaRequestPayload requestPayload,
        RequestAviationAccountInfo accountInfo,
        RequestTaskPayloadType payloadType
    );
}
