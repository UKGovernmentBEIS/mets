package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitSurrenderRequestActionPayloadMapper {

    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "reviewDecision", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationGrantedRequestActionPayload cloneGrantedPayloadIgnoreReasonAndNotes(
            PermitSurrenderApplicationGrantedRequestActionPayload requestActionPayload);
    
    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "reviewDecision", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReasonAndNotes(
            PermitSurrenderApplicationRejectedRequestActionPayload requestActionPayload);
    
    @Mapping(target = "reviewDetermination.reason", ignore = true)
    @Mapping(target = "reviewDecision", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReasonAndNotes(
            PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload requestActionPayload);

    @Mapping(target = "cessation.notes", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitCessationCompletedRequestActionPayload cloneCessationCompletedPayloadIgnoreNotes(
        PermitCessationCompletedRequestActionPayload requestActionPayload);
}
