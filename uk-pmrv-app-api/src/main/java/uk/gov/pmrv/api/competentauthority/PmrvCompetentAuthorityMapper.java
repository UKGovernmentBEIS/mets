package uk.gov.pmrv.api.competentauthority;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(
    componentModel = "spring",
    config = MapperConfig.class
)
public interface PmrvCompetentAuthorityMapper {

      @Mapping(target = "email", source = "email")
	  @Mapping(target = "name", source = "competentAuthority.name")
	  CompetentAuthorityDTO toCompetentAuthorityDTO(PmrvCompetentAuthority competentAuthority, String email);

}
