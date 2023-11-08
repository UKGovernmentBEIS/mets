package uk.gov.pmrv.api.competentauthority.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LockMode;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.domain.CompetentAuthority;

import java.util.Optional;

@Repository
public class CompetentAuthorityCustomRepositoryImpl implements CompetentAuthorityCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<CompetentAuthority> findByIdForUpdate(CompetentAuthorityEnum id) {
        return ((Query<CompetentAuthority>)entityManager.createQuery("select ca from CompetentAuthority ca where ca.id = :id"))
                .setLockMode("ac", LockMode.PESSIMISTIC_WRITE)
                .setTimeout(5000)
                .setParameter("id", id)
                .uniqueResultOptional();
    }
    
}
