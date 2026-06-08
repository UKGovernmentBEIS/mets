package uk.gov.pmrv.api.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.domain.AccountDetailsHistory;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryListResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountDetailsHistoryCategory;
import uk.gov.pmrv.api.account.repository.AccountDetailsHistoryRepository;
import uk.gov.pmrv.api.account.transform.AccountDetailsHistoryMapper;


@Service
@RequiredArgsConstructor
@Log4j2
public class AccountDetailsHistoryService {

    private final AccountDetailsHistoryRepository accountDetailsHistoryRepository;
    private final ObjectMapper objectMapper;
    private final AccountDetailsHistoryMapper accountDetailsHistoryMapper;

    public <T> void createAccountDetailsHistory(Long accountId, AccountDetailsHistoryCategory category,
                                                T previousValue, T newValue, String reason, AppUser appUser) {
        AccountDetailsHistory accountDetailsHistory =
            AccountDetailsHistory.builder()
                    .accountId(accountId)
                    .category(category)
                    .previousValue(objectMapper.valueToTree(previousValue))
                    .newValue(objectMapper.valueToTree(newValue))
                    .reason(reason)
                    .changedBy(appUser.getFullName())
                    .build();

        accountDetailsHistoryRepository.save(accountDetailsHistory);
    }

    public AccountDetailsHistoryListResponse getAccountDetailsHistory(Long accountId, Integer page, Integer size) {

        Page<AccountDetailsHistory> accountDetailsHistoryPage =
                accountDetailsHistoryRepository.findByAccountIdOrderByCreationDateDesc(PageRequest.of(page, size), accountId);

        return AccountDetailsHistoryListResponse.builder()
                .accountDetailsHistoryList(accountDetailsHistoryPage.get().map(accountDetailsHistoryMapper::toAccountDetailsHistoryDTO).toList())
                .total(accountDetailsHistoryPage.getTotalElements())
                .build();

    }

}
