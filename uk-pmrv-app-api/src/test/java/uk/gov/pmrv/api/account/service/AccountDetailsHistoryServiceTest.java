package uk.gov.pmrv.api.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.domain.AccountDetailsHistory;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryListResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountDetailsHistoryCategory;
import uk.gov.pmrv.api.account.repository.AccountDetailsHistoryRepository;
import uk.gov.pmrv.api.account.transform.AccountDetailsHistoryMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountDetailsHistoryServiceTest {

    @InjectMocks
    private AccountDetailsHistoryService service;

    @Mock
    private AccountDetailsHistoryRepository repository;

    @Mock
    private AccountDetailsHistoryMapper accountDetailsHistoryMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createAccountDetailsHistory_shouldSaveHistoryRecord() {

        Long accountId = 1L;
        AccountDetailsHistoryCategory category = AccountDetailsHistoryCategory.FIRST_YEAR_OF_REPORTING_OBLIGATION;
        String prev = "2024";
        String next = "2025";
        String reason = "test";
        AppUser appUser = new AppUser();

        service.createAccountDetailsHistory(accountId, category, prev, next, reason,appUser);

        ArgumentCaptor<AccountDetailsHistory> historyCaptor = ArgumentCaptor.forClass(AccountDetailsHistory.class);

        verify(repository, times(1)).save(historyCaptor.capture());

        AccountDetailsHistory savedRecord = historyCaptor.getValue();

        assertThat(savedRecord.getAccountId()).isEqualTo(accountId);
        assertThat(savedRecord.getCategory()).isEqualTo(category);
        assertThat(savedRecord.getReason()).isEqualTo(reason);

        assertThat(savedRecord.getPreviousValue()).isEqualTo(new TextNode("2024"));
        assertThat(savedRecord.getNewValue()).isEqualTo(new TextNode("2025"));
    }

    @Test
    void getAccountDetailsHistory_shouldReturnPagedResponse() {
        Long accountId = 1L;
        int page = 0;
        int size = 10;

        AccountDetailsHistory historyEntry = AccountDetailsHistory.builder()
                .accountId(accountId)
                .category(AccountDetailsHistoryCategory.FIRST_YEAR_OF_REPORTING_OBLIGATION)
                .reason("some reason")
                .build();

        AccountDetailsHistoryDTO historyDTO = AccountDetailsHistoryDTO.builder()
                .category(AccountDetailsHistoryCategory.FIRST_YEAR_OF_REPORTING_OBLIGATION)
                .reason("some reason")
                .build();

        PageImpl<AccountDetailsHistory> pagedResponse = new PageImpl<>(List.of(historyEntry));

        when(repository.findByAccountIdOrderByCreationDateDesc(PageRequest.of(page, size), accountId))
                .thenReturn(pagedResponse);
        when(accountDetailsHistoryMapper.toAccountDetailsHistoryDTO(historyEntry))
                .thenReturn(historyDTO);

        AccountDetailsHistoryListResponse result = service.getAccountDetailsHistory(accountId, page, size);

        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getAccountDetailsHistoryList()).containsExactly(historyDTO);
    }
}