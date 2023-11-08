package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;
import uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactsRepository;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class AviationAccountAssignedRegulatorSiteContactsRepository implements AccountAssignedRegulatorSiteContactsRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<AccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return entityManager.createNativeQuery("select account.emitter_id as \"accountId\", account.type as \"accountType\", " +
                " account.name as \"accountName\", acc_av.status as \"accountStatus\", le.name as \"legalEntityName\", " +
                " auth.status as \"authorityStatus\", acc_contact.user_id as \"userId\" " +
                " from account " +
                " inner join account_aviation acc_av on account.id = acc_av.id " +
                " left join account_legal_entity le on account.legal_entity_id = le.id " +
                " left join account_contact acc_contact on account.id = acc_contact.account_id and acc_contact.contact_type='CA_SITE' " +
                " left join au_authority auth on acc_contact.user_id = auth.user_id " +
                " order by acc_contact.user_id, acc_av.status, le.name, account.name asc")
            .unwrap(NativeQuery.class)
            .addScalar("accountId", StandardBasicTypes.STRING)
            .addScalar("accountType", StandardBasicTypes.STRING)
            .addScalar("accountName", StandardBasicTypes.STRING)
            .addScalar("accountStatus", StandardBasicTypes.STRING)
            .addScalar("legalEntityName", StandardBasicTypes.STRING)
            .addScalar("authorityStatus", StandardBasicTypes.STRING)
            .addScalar("userId", StandardBasicTypes.STRING)
            .setReadOnly(true)
            .setResultTransformer(Transformers.aliasToBean(AccountAssignedRegulatorSiteContact.class))
            .getResultList();
    }
}
