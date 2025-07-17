package uk.gov.pmrv.api.migration.permit.envpermitsandlicences;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EnvPermitOrLicenceMapper {

    List<uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence> toPermitEnvPermitOrLicences(List<EnvPermitOrLicence> etsEnvPermitOrLicence);
    
    @Mapping(target = "num", source = "permitNumber")
    @Mapping(target = "type", source = "permitType")
    @Mapping(target = "permitHolder", source = "permitHolder")
    @Mapping(target = "issuingAuthority", source = "issuingAuthority")
    uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence toPermitEnvPermitOrLicence(EnvPermitOrLicence etsEnvPermitOrLicence);
}
