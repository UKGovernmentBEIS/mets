package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.mapper;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaDynamicSection;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;

@UtilityClass
public class EmpCorsiaDynamicSectionReviewGroupMapper {

    private static final Map<EmpCorsiaDynamicSection, EmpCorsiaReviewGroup> MAP =
        ImmutableMap.<EmpCorsiaDynamicSection, EmpCorsiaReviewGroup>builder()
            .put(EmpCorsiaDynamicSection.METHOD_A_PROCEDURES, EmpCorsiaReviewGroup.METHOD_A_PROCEDURES)
            .put(EmpCorsiaDynamicSection.METHOD_B_PROCEDURES, EmpCorsiaReviewGroup.METHOD_B_PROCEDURES)
            .put(EmpCorsiaDynamicSection.BLOCK_ON_OFF_PROCEDURES, EmpCorsiaReviewGroup.BLOCK_ON_OFF_PROCEDURES)
            .put(EmpCorsiaDynamicSection.FUEL_UPLIFT_PROCEDURES, EmpCorsiaReviewGroup.FUEL_UPLIFT_PROCEDURES)
            .put(EmpCorsiaDynamicSection.BLOCK_HOUR_PROCEDURES, EmpCorsiaReviewGroup.BLOCK_HOUR_PROCEDURES)
            .build();

    public static EmpCorsiaReviewGroup getReviewGroupFromDynamicSection(EmpCorsiaDynamicSection section) {
        return MAP.get(section);
    }
}
