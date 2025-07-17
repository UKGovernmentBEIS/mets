package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpCorsiaMapper {

    @Mapping(target = "serviceContactDetails", source = "requestAviationAccountInfo.serviceContactDetails")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
    EmissionsMonitoringPlanCorsiaContainer toEmissionsMonitoringPlanCorsiaContainer(
        EmpIssuanceCorsiaRequestPayload requestPayload,
        RequestAviationAccountInfo requestAviationAccountInfo,
        EmissionTradingScheme scheme);
}
