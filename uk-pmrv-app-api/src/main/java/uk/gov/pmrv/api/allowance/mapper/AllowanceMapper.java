package uk.gov.pmrv.api.allowance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;
import uk.gov.pmrv.api.allowance.domain.AllowanceAllocationEntity;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.common.transform.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AllowanceMapper {

    @Mapping(target = "allocation", source = "allocation.allowances")
    AllowanceAllocationEntity toAllowanceAllocationEntity(PreliminaryAllocation allocation, Long accountId);

    @Mapping(target = "allowances", source = "allocation")
    PreliminaryAllocation toPreliminaryAllocation(AllowanceAllocationEntity entity);

    @Mapping(target = "amount", source = "activityLevel.changedActivityLevel")
    AllowanceActivityLevelEntity toActivityLevelEntity(ActivityLevel activityLevel, Long accountId);

    @Mapping(target = "amount", source = "activityLevel.changedActivityLevel")
    AllowanceActivityLevelEntity toActivityLevelEntity(HistoricalActivityLevel activityLevel, Long accountId);

    default List<AllowanceActivityLevelEntity> toActivityLevelEntities(List<ActivityLevel> activityLevels, Long accountId) {
        return activityLevels.stream()
                .map(activityLevel -> toActivityLevelEntity(activityLevel, accountId))
                .toList();
    }

    @Mapping(target = "changedActivityLevel", source = "amount")
    HistoricalActivityLevel toHistoricalActivityLevel(AllowanceActivityLevelEntity entity);
}
