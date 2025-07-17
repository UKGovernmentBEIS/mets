package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpCorsiaSubmitMapper {

    @Mapping(target = "scheme", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.CORSIA)")
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload empIssuanceCorsiaApplicationSubmitRequestTaskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "empAttachments", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.operatorName", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.managementProcedures.attachmentIds", ignore = true)
    EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload toEmpIssuanceCorsiaApplicationSubmittedRequestActionPayload(
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload empIssuanceCorsiaApplicationSubmitRequestTaskPayload, RequestAviationAccountInfo accountInfo);

    @AfterMapping
    default void setOperatorDetailsName(@MappingTarget EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload requestActionPayload,
                                                   RequestAviationAccountInfo accountInfo) {
        EmpCorsiaOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setOperatorName(accountInfo.getOperatorName());
    }

    @AfterMapping
    default void setEmpAttachments(@MappingTarget EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload requestActionPayload,
                                   EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setEmpAttachments(taskPayload.getEmpAttachments());
    }
}
