package uk.gov.pmrv.api.competentauthority.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.domain.CompetentAuthority;

public interface CompetentAuthorityRepository extends JpaRepository<CompetentAuthority, Long>, CompetentAuthorityCustomRepository {

	CompetentAuthority findById(CompetentAuthorityEnum id);
	
}
