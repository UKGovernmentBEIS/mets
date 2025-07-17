package uk.gov.pmrv.api.mireport.aviation.accountsregulatorsitecontacts;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContact;
import uk.gov.netz.api.mireport.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AviationAccountAssignedRegulatorSiteContactsRepository implements AccountAssignedRegulatorSiteContactsRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<AviationAccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager) {
        return entityManager.createNativeQuery("select account.emitter_id as \"accountId\", account.type as \"accountType\", " +
                        " account.name as \"accountName\", acc_av.status as \"accountStatus\", le.name as \"legalEntityName\", " +
                        " auth.status as \"authorityStatus\", acc_contact.user_id as \"userId\", acc_av.central_route_charges_office_code as \"crcoCode\" " +
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
                .addScalar("crcoCode", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer((tuple, aliases) -> {
                    Map<String, Object> map = new HashMap<>();
                    for(int i = 0; i < tuple.length; i++) {
                        map.put(aliases[i], tuple[i]);
                    }
                    AviationAccountAssignedRegulatorSiteContact result = new AviationAccountAssignedRegulatorSiteContact();
                    result.setAccountId((String)map.get("accountId"));
                    result.setAccountType((String)map.get("accountType"));
                    result.setAccountName((String)map.get("accountName"));
                    result.setAccountStatus((String)map.get("accountStatus"));
                    result.setLegalEntityName((String)map.get("legalEntityName"));
                    result.setAuthorityStatus((String)map.get("authorityStatus"));
                    result.setUserId((String)map.get("userId"));
                    result.setCrcoCode((String)map.get("crcoCode"));
                    return result;
                })
                .getResultList();
    }
}
