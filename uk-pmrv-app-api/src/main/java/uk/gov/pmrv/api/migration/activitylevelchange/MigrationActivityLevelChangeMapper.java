package uk.gov.pmrv.api.migration.activitylevelchange;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.common.transform.MapperConfig;

import java.util.Arrays;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationActivityLevelChangeMapper {


    @Mapping(target = "year", source = "historyYear")
    @Mapping(target = "creationDate", source = "endDate")
    @Mapping(target = "subInstallationName", ignore = true)
    @Mapping(target = "changeType", ignore = true)
    HistoricalActivityLevel toActivityLevel(ActivityLevelChangeVO activityLevelChangeVO);

    @AfterMapping
    default void setSubInstallationName(@MappingTarget ActivityLevel activityLevel, ActivityLevelChangeVO activityLevelChangeVO) {
        Arrays.stream(SubInstallationName.values())
            .filter(subInstallationName -> subInstallationName.getDescription().equalsIgnoreCase(activityLevelChangeVO.getSubInstallationName()))
            .findFirst()
            .ifPresent(activityLevel::setSubInstallationName);
    }

    @AfterMapping
    default void setChangeType(@MappingTarget ActivityLevel activityLevel, ActivityLevelChangeVO activityLevelChangeVO) {
        activityLevel.setChangeType(ChangeType.OTHER);
        activityLevel.setOtherChangeTypeName(activityLevelChangeVO.getChangeType());
    }
}
