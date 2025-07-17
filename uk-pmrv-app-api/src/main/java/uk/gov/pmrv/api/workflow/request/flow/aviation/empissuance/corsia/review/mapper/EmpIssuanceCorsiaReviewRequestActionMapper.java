package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpIssuanceCorsiaReviewRequestActionMapper {

    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "reviewGroupDecisions", ignore = true)
    EmpIssuanceCorsiaApplicationApprovedRequestActionPayload cloneApprovedPayloadIgnoreReasonAndDecisions(
        EmpIssuanceCorsiaApplicationApprovedRequestActionPayload payload);

    @Mapping(target = "determination.reason", ignore = true)
    EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload payload);
}
