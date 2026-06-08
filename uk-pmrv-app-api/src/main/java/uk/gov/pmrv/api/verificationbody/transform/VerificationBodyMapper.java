package uk.gov.pmrv.api.verificationbody.transform;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface VerificationBodyMapper {

    VerificationBodyDTO toVerificationBodyDTO(VerificationBody verificationBody);

    @Mapping(target = "verificationBodyEmissionSchemes", source = "emissionSchemes")
    VerificationBodyDTO toVerificationBodyDTOWithEmissionSchemes(VerificationBody verificationBody);

    VerificationBodyInfoDTO toVerificationBodyInfoDTO(VerificationBody verificationBody);

    List<VerificationBodyInfoDTO> toVerificationBodyInfoDTO(List<VerificationBody> verificationBodies);
    
    VerificationBodyNameInfoDTO toVerificationBodyNameInfoDTO(VerificationBody verificationBody);

    @Mapping(target = "emissionSchemes", source = "verificationBodyEmissionSchemes")
    VerificationBody toVerificationBody(VerificationBodyEditDTO verificationBodyEditDTO);

    @Mapping(target = "verificationBodyEmissionSchemeDTOS", source = "verificationBodyEmissionSchemes")
    VerificationBodyDetails toVerificationBodyDetails(VerificationBodyDTO verificationBodyDTO);
}
