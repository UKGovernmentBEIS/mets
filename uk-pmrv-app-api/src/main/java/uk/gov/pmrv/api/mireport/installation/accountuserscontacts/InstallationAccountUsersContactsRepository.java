package uk.gov.pmrv.api.mireport.installation.accountuserscontacts;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUsersContactsRepository;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class InstallationAccountUsersContactsRepository implements AccountUsersContactsRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<AccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return entityManager.createNativeQuery(
                        "select auth.user_id as \"userId\", role.name as \"role\",  account.emitter_id as \"accountId\", account.name as \"accountName\", " +
                                "account.type as \"accountType\", acc_inst.status as \"accountStatus\", le.name as \"legalEntityName\", auth.status as \"authorityStatus\",\n" +
                                "       case when acPrimary.user_id is not null then true else false end as \"primaryContact\",\n" +
                                "       case when acService.user_id is not null then true else false end as \"secondaryContact\",\n" +
                                "       case when acFinancial.user_id is not null then true else false end as \"financialContact\",\n" +
                                "       case when acSecondary.user_id is not null then true else false end as \"serviceContact\",\n" +
                                "       permit.id as \"permitId\", permit.data->> 'permitType' as \"permitType\"\n" +
                                "from account\n" +
                                "    inner join account_installation acc_inst on account.id = acc_inst.id " +
                                "    inner join account_legal_entity le on account.legal_entity_id = le.id\n" +
                                "    left join permit on account.id = permit.account_id\n" +
                                "    left join au_authority auth on account.id = auth.account_id\n" +
                                "    left join au_role role on auth.code = role.code\n" +
                                "    left join account_contact acPrimary on account.id = acPrimary.account_id and auth.user_id=acPrimary.user_id and acPrimary.contact_type='PRIMARY'\n" +
                                "    left join account_contact acService on account.id = acService.account_id and auth.user_id=acService.user_id and acService.contact_type='SERVICE'\n" +
                                "    left join account_contact acFinancial on account.id = acFinancial.account_id and auth.user_id=acFinancial.user_id and acFinancial.contact_type='FINANCIAL'\n" +
                                "    left join account_contact acSecondary on account.id = acSecondary.account_id and auth.user_id=acSecondary.user_id and acSecondary.contact_type='SECONDARY'\n")
                .unwrap(NativeQuery.class)
                .addScalar("userId", StandardBasicTypes.STRING)
                .addScalar("accountId", StandardBasicTypes.STRING)
                .addScalar("accountName", StandardBasicTypes.STRING)
                .addScalar("accountType", StandardBasicTypes.STRING)
                .addScalar("accountStatus", StandardBasicTypes.STRING)
                .addScalar("legalEntityName", StandardBasicTypes.STRING)
                .addScalar("authorityStatus", StandardBasicTypes.STRING)
                .addScalar("primaryContact", StandardBasicTypes.BOOLEAN)
                .addScalar("secondaryContact", StandardBasicTypes.BOOLEAN)
                .addScalar("financialContact", StandardBasicTypes.BOOLEAN)
                .addScalar("serviceContact", StandardBasicTypes.BOOLEAN)
                .addScalar("permitId", StandardBasicTypes.STRING)
                .addScalar("permitType", StandardBasicTypes.STRING)
                .addScalar("role", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer(Transformers.aliasToBean(AccountUserContact.class)) //https://vladmihalcea.com/hibernate-resulttransformer/
                .getResultList();
    }
}
