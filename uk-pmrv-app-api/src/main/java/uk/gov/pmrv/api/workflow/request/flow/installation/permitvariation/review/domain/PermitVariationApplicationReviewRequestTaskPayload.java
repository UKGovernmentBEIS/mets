package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadDecidableAndDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitVariationApplicationReviewRequestTaskPayload extends PermitVariationApplicationSubmitRequestTaskPayload
    implements RequestTaskPayloadRfiAttachable,
    PermitPayloadDecidableAndDeterminateable<PermitVariationReviewDecision, PermitVariationDeterminateable> {

    private PermitContainer originalPermitContainer;

    private PermitVariationReviewDecision permitVariationDetailsReviewDecision;
    
    private Boolean permitVariationDetailsReviewCompleted;

    @Builder.Default
    private Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    private PermitVariationDeterminateable determination;

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getReviewAttachments(), getRfiAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
    	final Set<UUID> reviewGroupsReviewAttachmentsUuids = getReviewGroupDecisions().values().stream()
    			.filter(permitVariationReviewDecision -> permitVariationReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .map(PermitVariationReviewDecision::getDetails)
                .flatMap(details -> ((ChangesRequiredDecisionDetails)details).getRequiredChanges().stream())
                .flatMap(change -> change.getFiles().stream())
                .collect(Collectors.toSet());
    	
    	final Set<UUID> variationDetailsReviewAttachmentsUuids = new HashSet<>();
		if (permitVariationDetailsReviewDecision != null
				&& permitVariationDetailsReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED) {
			variationDetailsReviewAttachmentsUuids
					.addAll(((ChangesRequiredDecisionDetails) permitVariationDetailsReviewDecision.getDetails())
							.getRequiredChanges().stream()
							.flatMap(change -> change.getFiles().stream())
							.collect(Collectors.toSet()));
		}
    	
        return Stream.of(super.getReferencedAttachmentIds(), reviewGroupsReviewAttachmentsUuids, variationDetailsReviewAttachmentsUuids)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getPermitAttachments().keySet().removeIf(uuids::contains);
        getReviewAttachments().keySet().removeIf(uuids::contains);
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }

}
