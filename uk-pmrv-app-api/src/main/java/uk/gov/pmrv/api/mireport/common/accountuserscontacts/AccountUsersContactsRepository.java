package uk.gov.pmrv.api.mireport.common.accountuserscontacts;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface AccountUsersContactsRepository {

    List<AccountUserContact> findAccountUserContacts(EntityManager entityManager);
}
