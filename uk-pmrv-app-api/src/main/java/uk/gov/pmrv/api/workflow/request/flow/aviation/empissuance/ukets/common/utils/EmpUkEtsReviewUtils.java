package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsDynamicSection;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.mapper.EmpUkEtsDynamicSectionReviewGroupMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class EmpUkEtsReviewUtils {

    public Set<EmpUkEtsReviewGroup> getEmpUkEtsReviewGroups(EmissionsMonitoringPlanUkEts emp){
        final Set<EmpUkEtsReviewGroup> empDynamicSectionReviewGroups = emp.getNotEmptyDynamicSections().stream()
            .map(EmpUkEtsDynamicSectionReviewGroupMapper::getReviewGroupFromDynamicSection)
            .collect(Collectors.toSet());

        return Stream.of(
                EmpUkEtsReviewGroup.getStandardReviewGroups(),
                empDynamicSectionReviewGroups)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }
    
    public Set<EmpUkEtsReviewGroup> getDeprecatedReviewGroupDecisions(Set<EmpUkEtsDynamicSection> existingDynamicSections,
    		Set<EmpUkEtsDynamicSection> newDynamicSections) {
		
		Set<EmpUkEtsDynamicSection> removedDynamicSections = new HashSet<>(existingDynamicSections);
		Set<EmpUkEtsReviewGroup> deprecatedReviewGroups = new HashSet<>();		
		removedDynamicSections.removeAll(newDynamicSections);
		
		if (!removedDynamicSections.isEmpty()) {
			deprecatedReviewGroups = removedDynamicSections.stream()
			.map(EmpUkEtsDynamicSectionReviewGroupMapper::getReviewGroupFromDynamicSection)
			.collect(Collectors.toSet());
		}
		return deprecatedReviewGroups;
    }
}
