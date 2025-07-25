package uk.gov.pmrv.api.verificationbody.transform;

import java.util.List;
import org.mapstruct.Mapper;
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

    VerificationBodyInfoDTO toVerificationBodyInfoDTO(VerificationBody verificationBody);

    List<VerificationBodyInfoDTO> toVerificationBodyInfoDTO(List<VerificationBody> verificationBodies);
    
    VerificationBodyNameInfoDTO toVerificationBodyNameInfoDTO(VerificationBody verificationBody);

    VerificationBody toVerificationBody(VerificationBodyEditDTO verificationBodyEditDTO);

    VerificationBodyDetails toVerificationBodyDetails(VerificationBodyDTO verificationBodyDTO);
}
