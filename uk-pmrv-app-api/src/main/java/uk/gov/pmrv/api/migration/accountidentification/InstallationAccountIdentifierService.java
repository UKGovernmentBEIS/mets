package uk.gov.pmrv.api.migration.accountidentification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.AccountIdentifier;
import uk.gov.pmrv.api.account.repository.AccountIdentifierRepository;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class InstallationAccountIdentifierService {
    private final AccountIdentifierRepository accountIdentifierRepository;

    @Transactional
    public void updateAccountIdentifier(Long maxAccountId) {
        AccountIdentifier identifier = accountIdentifierRepository.findAccountIdentifier()
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        identifier.setAccountId(maxAccountId != null ? maxAccountId : 0);
    }
}
