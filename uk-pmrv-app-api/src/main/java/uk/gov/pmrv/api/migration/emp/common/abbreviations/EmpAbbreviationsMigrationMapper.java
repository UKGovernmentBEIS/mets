package uk.gov.pmrv.api.migration.emp.common.abbreviations;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpAbbreviationsMigrationMapper {

    List<EmpAbbreviationDefinition> toEmpAbbreviationDefinitions(List<EtsEmpAbbreviation> etsEmpAbbreviations);
}
