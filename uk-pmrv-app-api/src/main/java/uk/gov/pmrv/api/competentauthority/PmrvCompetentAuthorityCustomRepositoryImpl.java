package uk.gov.pmrv.api.competentauthority;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LockMode;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.netz.api.competentauthority.CompetentAuthority;
import uk.gov.netz.api.competentauthority.CompetentAuthorityCustomRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Optional;

@Repository
public class PmrvCompetentAuthorityCustomRepositoryImpl implements CompetentAuthorityCustomRepository<PmrvCompetentAuthority> {

	@PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<PmrvCompetentAuthority> findByIdForUpdate(CompetentAuthorityEnum id) {
        return ((Query<PmrvCompetentAuthority>)entityManager.createQuery("select ca from PmrvCompetentAuthority ca where ca.id = :id"))
                .setLockMode("ac", LockMode.PESSIMISTIC_WRITE)
                .setTimeout(5000)
                .setParameter("id", id)
                .uniqueResultOptional();
    }
    
}
