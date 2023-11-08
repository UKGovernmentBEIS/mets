package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaDynamicSection;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.mapper.EmpCorsiaDynamicSectionReviewGroupMapper;

@UtilityClass
public class EmpCorsiaReviewUtils {

    public Set<EmpCorsiaReviewGroup> getEmpCorsiaReviewGroups(EmissionsMonitoringPlanCorsia emp){
        final Set<EmpCorsiaReviewGroup> empDynamicSectionReviewGroups = emp.getNotEmptyDynamicSections().stream()
            .map(EmpCorsiaDynamicSectionReviewGroupMapper::getReviewGroupFromDynamicSection)
            .collect(Collectors.toSet());

        return Stream.of(
                EmpCorsiaReviewGroup.getStandardReviewGroups(),
                empDynamicSectionReviewGroups)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }
    
    public Set<EmpCorsiaReviewGroup> getDeprecatedReviewGroupDecisions(Set<EmpCorsiaDynamicSection> existingDynamicSections,
    		Set<EmpCorsiaDynamicSection> newDynamicSections) {
		
		Set<EmpCorsiaDynamicSection> removedDynamicSections = new HashSet<>(existingDynamicSections);
		Set<EmpCorsiaReviewGroup> deprecatedReviewGroups = new HashSet<>();		
		removedDynamicSections.removeAll(newDynamicSections);
		
		if (!removedDynamicSections.isEmpty()) {
			deprecatedReviewGroups = removedDynamicSections.stream()
			.map(EmpCorsiaDynamicSectionReviewGroupMapper::getReviewGroupFromDynamicSection)
			.collect(Collectors.toSet());
		}
		return deprecatedReviewGroups;
    }
}
