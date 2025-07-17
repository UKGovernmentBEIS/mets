package uk.gov.pmrv.api.migration.permit.abbreviations;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationAbbreviationsMapper {

    AbbreviationDefinition toAbbreviationDefinition(EtsAbbreviation etsAbbreviation);

    List<AbbreviationDefinition> toAbbreviationDefinitions(List<EtsAbbreviation> etsAbbreviations);
}
