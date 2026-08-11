package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserReportEntryRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<UserReportEntry> findUserReportEntries(EntityManager entityManager, AccountType accountType) {

        String accountLine = "";
        if (AccountType.INSTALLATION.equals(accountType)) {
            accountLine = "inner join account_installation acc_2 on acc.id = acc_2.id ";
        } else {
            accountLine = "inner join account_aviation acc_2 on acc.id = acc_2.id ";
        }

        return entityManager.createNativeQuery("select auth.user_id as \"userAccountId\"," +
                        "auth.code as \"role\", " +
                        "auth.status as \"userAccountStatus\", " +
                        "COALESCE(string_agg(DISTINCT account_contact.contact_type, ','), '') as \"contactTypes\" " +
                        "from account acc " +
                        "inner join au_authority auth on acc.id = auth.account_id " +
                        "left join account_contact on acc.id = account_contact.account_id and auth.user_id = account_contact.user_id " +
                         accountLine +
                        "where acc.type = :accountType " +
                        "group by acc.type, acc.competent_authority, auth.user_id, auth.code, auth.status")
                .unwrap(NativeQuery.class)
                .setParameter("accountType", accountType.name())
                .addScalar("userAccountId", StandardBasicTypes.STRING)
                .addScalar("role", StandardBasicTypes.STRING)
                .addScalar("userAccountStatus", StandardBasicTypes.STRING)
                .addScalar("contactTypes", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer((tuple, aliases) -> {
                    UserReportEntry result = new UserReportEntry();
                    result.setUserAccountId((String) tuple[0]);
                    result.setRole((String) tuple[1]);
                    result.setUserAccountStatus((String) tuple[2]);
                    result.setContactTypes(
                            Optional.ofNullable((String) tuple[3])
                                    .filter(s -> !s.isBlank())
                                    .map(s -> List.of(s.split(",")))
                                    .orElse(Collections.emptyList())
                    );
                    return result;
                }).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Authority> findByCompetentAuthorityIsNotNull(EntityManager entityManager) {
        return entityManager.createNativeQuery(
        """
            select * from au_authority auth
            where auth.competent_authority is not null
        """, Authority.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Authority> findByCodeIn(EntityManager entityManager, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }

        return entityManager.createNativeQuery(
            """
                select * from au_authority auth
                where auth.code in (:codes)
            """, Authority.class)
            .setParameter("codes", codes).getResultList();
    }

}
