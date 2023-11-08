package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpIssuanceUkEtsReviewRequestActionMapper {

    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "reviewGroupDecisions", ignore = true)
    EmpIssuanceUkEtsApplicationApprovedRequestActionPayload cloneApprovedPayloadIgnoreReasonAndDecisions(
        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload payload);

    @Mapping(target = "determination.reason", ignore = true)
    EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload payload);
}
