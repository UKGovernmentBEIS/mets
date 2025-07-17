package uk.gov.pmrv.api.mireport.installation.accountsregulatorsitecontacts;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InstallationAccountAssignedRegulatorSiteContactsRepository implements AccountAssignedRegulatorSiteContactsRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<InstallationAccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {

        return entityManager.createNativeQuery("select account.emitter_id as \"accountId\", account.type as \"accountType\", " +
                        " account.name as \"accountName\", acc_inst.status as \"accountStatus\", le.name as \"legalEntityName\", " +
                        " auth.status as \"authorityStatus\", acc_contact.user_id as \"userId\", " +
                        " acc_inst.emitter_type as \"emitterType\" " +
                        " from account " +
                        " inner join account_installation acc_inst on account.id = acc_inst.id " +
                        " inner join account_legal_entity le on account.legal_entity_id = le.id " +
                        " left join account_contact acc_contact on account.id = acc_contact.account_id and acc_contact.contact_type='CA_SITE' " +
                        " left join au_authority auth on acc_contact.user_id = auth.user_id " +
                        " left join permit on account.id = permit.account_id\n" +
                        " order by acc_contact.user_id, acc_inst.status, le.name, account.name asc")
                .unwrap(NativeQuery.class)
                .addScalar("accountId", StandardBasicTypes.STRING)
                .addScalar("accountType", StandardBasicTypes.STRING)
                .addScalar("accountName", StandardBasicTypes.STRING)
                .addScalar("accountStatus", StandardBasicTypes.STRING)
                .addScalar("legalEntityName", StandardBasicTypes.STRING)
                .addScalar("authorityStatus", StandardBasicTypes.STRING)
                .addScalar("userId", StandardBasicTypes.STRING)
                .addScalar("emitterType", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer((tuple, aliases) -> {
                    Map<String, Object> map = new HashMap<>();
                    for(int i = 0; i < tuple.length; i++) {
                        map.put(aliases[i], tuple[i]);
                    }
                    InstallationAccountAssignedRegulatorSiteContact result = new InstallationAccountAssignedRegulatorSiteContact();
                    result.setAccountId((String)map.get("accountId"));
                    result.setAccountType((String)map.get("accountType"));
                    result.setAccountName((String)map.get("accountName"));
                    result.setAccountStatus((String)map.get("accountStatus"));
                    result.setLegalEntityName((String)map.get("legalEntityName"));
                    result.setAuthorityStatus((String)map.get("authorityStatus"));
                    result.setUserId((String)map.get("userId"));
                    result.setEmitterType((String)map.get("emitterType"));
                    return result;
                })
                .getResultList();
    }
}
