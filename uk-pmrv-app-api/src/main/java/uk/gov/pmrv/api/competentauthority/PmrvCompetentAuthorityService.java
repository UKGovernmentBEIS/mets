package uk.gov.pmrv.api.competentauthority;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.competentauthority.CompetentAuthority;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.competentauthority.CompetentAuthorityMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;

@Primary
@Service
@Log4j2
public class PmrvCompetentAuthorityService extends CompetentAuthorityService {

    public PmrvCompetentAuthorityService(CompetentAuthorityRepository<CompetentAuthority> competentAuthorityRepository,
                                         CompetentAuthorityMapper competentAuthorityMapper) {
        super(competentAuthorityRepository, competentAuthorityMapper);
    }
    
    @Override
    public CompetentAuthorityDTO getCompetentAuthorityDTO(CompetentAuthorityEnum competentAuthorityEnum) {
        // use CompetentAuthorityDTOByRequestResolver instead because we need to extract CA's email from request
        throw new UnsupportedOperationException("not supported");
    }

}
