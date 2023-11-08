package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.mapper;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsDynamicSection;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;

import java.util.Map;

@UtilityClass
public class EmpUkEtsDynamicSectionReviewGroupMapper {

    private static final Map<EmpUkEtsDynamicSection, EmpUkEtsReviewGroup> MAP =
        ImmutableMap.<EmpUkEtsDynamicSection, EmpUkEtsReviewGroup>builder()
            .put(EmpUkEtsDynamicSection.METHOD_A_PROCEDURES, EmpUkEtsReviewGroup.METHOD_A_PROCEDURES)
            .put(EmpUkEtsDynamicSection.METHOD_B_PROCEDURES, EmpUkEtsReviewGroup.METHOD_B_PROCEDURES)
            .put(EmpUkEtsDynamicSection.BLOCK_ON_OFF_PROCEDURES, EmpUkEtsReviewGroup.BLOCK_ON_OFF_PROCEDURES)
            .put(EmpUkEtsDynamicSection.FUEL_UPLIFT_PROCEDURES, EmpUkEtsReviewGroup.FUEL_UPLIFT_PROCEDURES)
            .put(EmpUkEtsDynamicSection.BLOCK_HOUR_PROCEDURES, EmpUkEtsReviewGroup.BLOCK_HOUR_PROCEDURES)
            .put(EmpUkEtsDynamicSection.DATA_GAPS, EmpUkEtsReviewGroup.DATA_GAPS)
            .build();

    public static EmpUkEtsReviewGroup getReviewGroupFromDynamicSection(EmpUkEtsDynamicSection section) {
        return MAP.get(section);
    }
}
