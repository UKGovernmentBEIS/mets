package uk.gov.pmrv.api.competentauthority;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityCustomRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

public interface PmrvCompetentAuthorityRepository
		extends JpaRepository<PmrvCompetentAuthority, CompetentAuthorityEnum>, CompetentAuthorityCustomRepository<PmrvCompetentAuthority> {

}
