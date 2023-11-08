package uk.gov.pmrv.api.account.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LockMode;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.account.domain.Account;

import java.util.Optional;

@Repository
public class AccountCustomRepositoryImpl implements AccountCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Account> findByIdForUpdate(Long id) {
        return ((Query<Account>)entityManager.createQuery("select ac from Account ac where ac.id = :id"))
                .setLockMode("ac", LockMode.PESSIMISTIC_WRITE)
                .setTimeout(5000)
                .setParameter("id", id)
                .uniqueResultOptional();
    }
}
