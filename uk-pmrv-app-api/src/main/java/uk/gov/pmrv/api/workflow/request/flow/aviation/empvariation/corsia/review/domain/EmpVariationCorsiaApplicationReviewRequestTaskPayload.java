package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.CollectionUtils;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaApplicationReviewRequestTaskPayload extends EmpVariationCorsiaApplicationSubmitRequestTaskPayload 
	implements RequestTaskPayloadRfiAttachable {

	private EmissionsMonitoringPlanCorsiaContainer originalEmpContainer;
	
	private EmpVariationReviewDecision empVariationDetailsReviewDecision;
	
	private Boolean empVariationDetailsReviewCompleted;

	@Builder.Default
    private Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
	
	private EmpVariationDetermination determination;
	
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
            .filter(empVariationReviewDecision -> empVariationReviewDecision.getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                empVariationReviewDecision -> ((ChangesRequiredDecisionDetails) empVariationReviewDecision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        final Set<UUID> variationDetailsReviewAttachmentsUuids = new HashSet<>();
        if (empVariationDetailsReviewDecision != null
            && empVariationDetailsReviewDecision.getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED) {
            variationDetailsReviewAttachmentsUuids
                .addAll(((ChangesRequiredDecisionDetails) empVariationDetailsReviewDecision.getDetails())
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
        getEmpAttachments().keySet().removeIf(uuids::contains);
        getReviewAttachments().keySet().removeIf(uuids::contains);
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }	
}
