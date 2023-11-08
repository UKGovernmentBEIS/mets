package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper;

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
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitTransferMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "transferAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    PermitTransferAApplicationSubmittedRequestActionPayload toPermitTransferAApplicationSubmitted(
        PermitTransferAApplicationRequestTaskPayload taskPayload
    );

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    @Mapping(target = "permitAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    PermitTransferBApplicationSubmittedRequestActionPayload toPermitTransferBApplicationSubmitted(
        PermitTransferBApplicationRequestTaskPayload taskPayload, InstallationOperatorDetails installationOperatorDetails
    );

    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    PermitContainer toPermitContainer(PermitTransferBApplicationRequestTaskPayload payload, InstallationOperatorDetails installationOperatorDetails);

    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    @Mapping(target = "payloadType", source = "payloadType")
    PermitTransferBApplicationReviewRequestTaskPayload toPermitTransferBApplicationReviewRequestTaskPayload(
        PermitTransferBRequestPayload permitTransferBRequestPayload, 
        InstallationOperatorDetails installationOperatorDetails,
        RequestTaskPayloadType payloadType
    );

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", source = "installationOperatorDetails")
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    PermitTransferBApplicationAmendsSubmitRequestTaskPayload toPermitTransferBApplicationAmendsSubmitRequestTaskPayload(PermitTransferBRequestPayload requestPayload, InstallationOperatorDetails installationOperatorDetails);
    
    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget
                                           final PermitTransferBApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
                                           final PermitTransferBRequestPayload payload) {
        
        final Set<UUID> amendFiles = requestTaskPayload.getReviewGroupDecisions().values().stream()
            .filter(permitReviewDecision -> permitReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                permitReviewDecision -> ((ChangesRequiredDecisionDetails) permitReviewDecision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

        final Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
            .filter(entry -> amendFiles.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestTaskPayload.setReviewAttachments(reviewFiles);
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<PermitReviewGroup, PermitIssuanceReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecision) {
        return reviewGroupDecision.entrySet().stream()
            .filter(entry -> entry.getValue().getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                    PermitIssuanceReviewDecision.builder()
                        .type(entry.getValue().getType())
                        .details(ChangesRequiredDecisionDetails.builder()
                            .requiredChanges(((ChangesRequiredDecisionDetails) entry.getValue().getDetails()).getRequiredChanges()).build())
                        .build())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_GRANTED_PAYLOAD)")
    @Mapping(target = "determination", ignore = true)
    PermitTransferBApplicationGrantedRequestActionPayload toPermitTransferBApplicationGrantedRequestActionPayload(
        PermitTransferBRequestPayload requestPayload
    );

    @AfterMapping
    default void setGrantDetermination(@MappingTarget PermitTransferBApplicationGrantedRequestActionPayload grantDeterminationRequestActionPayload,
                                       PermitTransferBRequestPayload requestPayload) {
        if (DeterminationType.GRANTED.equals(requestPayload.getDetermination().getType())) {
            grantDeterminationRequestActionPayload.setDetermination((PermitIssuanceGrantDetermination) requestPayload.getDetermination());
        }
    }

    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "reviewGroupDecisions", ignore = true)
    @Mapping(target = "permitTransferDetailsConfirmationDecision", ignore = true)
    PermitTransferBApplicationGrantedRequestActionPayload cloneGrantedPayloadIgnoreReasonAndDecisions(
        PermitTransferBApplicationGrantedRequestActionPayload payload
    );
}
