package uk.gov.pmrv.api.aviationreporting.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportableEmissionsDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationReportableEmissionsMapper {

    AviationReportableEmissionsDTO toAviationReportableEmissionsDTO(AviationReportableEmissionsEntity entity);

    @AfterMapping
    default void populateAttributesToUserRepresentation(@MappingTarget AviationReportableEmissionsDTO dto, AviationReportableEmissionsEntity entity) {
        if(ObjectUtils.isEmpty(entity.getReportableOffsetEmissions())) {
            dto.setTotalReportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                    .reportableEmissions(entity.getReportableEmissions())
                    .build());
        }
        else {
            dto.setTotalReportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                    .reportableEmissions(entity.getReportableEmissions())
                    .reportableOffsetEmissions(entity.getReportableOffsetEmissions())
                    .reportableReductionClaimEmissions(entity.getReportableReductionClaimEmissions())
                    .build());
        }
    }
}
