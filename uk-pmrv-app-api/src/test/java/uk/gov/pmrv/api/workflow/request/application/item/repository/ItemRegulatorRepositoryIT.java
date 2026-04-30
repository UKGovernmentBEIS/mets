package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.bpmn.WorkflowEngineType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemOrderBy;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemRegulatorRepository.class})
class ItemRegulatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemRegulatorRepository cut;

    @Autowired
    private EntityManager entityManager;

    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        accounts = createAccounts();
    }

    @Test
    void findItems_assigned_to_me() {
        Long account = accounts.get(0).getId();
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask2 =
                createRequestTask(user, request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t2", request2.getCreationDate());


        Request request3 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE, "t3", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, null);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Item item2 = itemPage.getItems().get(0);
        assertThat(item2.getRequestId()).isEqualTo(request2.getId());
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request2.getId());
        assertEquals(item2.getRequestType(), request2.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account);
        assertTrue(item2.isNew());

        Item item1 = itemPage.getItems().get(1);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account);
        assertFalse(item1.isNew());
    }

    @Test
    void findItems_orderByNewestFirst_returnItemsSortedByStartDateDesc() {
        Long account1 = accounts.get(0).getId();
        Long account1_2 = accounts.get(1).getId();

        String user = "user";


        LocalDateTime t1 = LocalDateTime.of(2024, 12, 5, 3, 45);
        LocalDateTime t2 = LocalDateTime.of(2024, 12, 6, 3, 45);
        LocalDateTime t3 = LocalDateTime.of(2024, 12, 9, 3, 45);
        LocalDateTime t4 = LocalDateTime.of(2024, 12, 8, 3, 45);
        LocalDateTime t5 = LocalDateTime.of(2024, 12, 7, 3, 45);
        LocalDateTime t6 = LocalDateTime.of(2024, 12, 15, 3, 45);
        LocalDateTime t7 = LocalDateTime.of(2025, 7, 15, 3, 45);


        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND,
                        Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", t1);
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request2, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", t2);

        Request request3 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t3", t3);

        Request request4 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", t4);


        Request request5 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request5, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", t5);


        Request request6 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request6, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", t6);


        Request request7 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request7, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t7", t7);


        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, null);

        assertEquals(7L, itemPage.getTotalItems());
        assertEquals(7, itemPage.getItems().size());

        assertThat(itemPage.getItems().stream().map(Item::getCreationDate)).containsExactly(t7, t6, t3, t4, t5, t2, t1);
    }

    @Test
    void findItems_orderByNearestDueDate_returnItemsSortedByDueDateAsc() {
        Long account1 = accounts.get(0).getId();
        Long account1_2 = accounts.get(1).getId();

        String user = "user";


        LocalDateTime t1 = LocalDateTime.of(2024, 12, 5, 3, 45);
        LocalDateTime t2 = LocalDateTime.of(2024, 12, 6, 3, 45);
        LocalDateTime t3 = LocalDateTime.of(2024, 12, 9, 3, 45);
        LocalDateTime t4 = LocalDateTime.of(2024, 12, 8, 3, 45);
        LocalDateTime t5 = LocalDateTime.of(2024, 12, 7, 3, 45);
        LocalDateTime t6 = LocalDateTime.of(2024, 12, 15, 3, 45);
        LocalDateTime t7 = LocalDateTime.of(2025, 7, 15, 3, 45);


        LocalDate d1 = LocalDate.of(2024, 12, 5);
        LocalDate d2 = LocalDate.of(2024, 12, 6);
        LocalDate d3 = LocalDate.of(2024, 12, 3);
        LocalDate d4 = LocalDate.of(2024, 12, 4);
        LocalDate d5 = LocalDate.of(2024, 12, 10);
        LocalDate d6 = LocalDate.of(2024, 12, 12);
        LocalDate d7 = LocalDate.of(2024, 12, 11);


        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND,
                        Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", t1, d1);
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request2, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", t2, d2);

        Request request3 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t3", t3, d3);

        Request request4 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", t4, d4);


        Request request5 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request5, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", t5, d5);


        Request request6 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request6, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", t6, d6);


        Request request7 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request7, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t7", t7, d7);


        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEAREST_DUE_DATE, null, null);

        assertEquals(7L, itemPage.getTotalItems());
        assertEquals(7, itemPage.getItems().size());

        assertThat(itemPage.getItems().stream().map(Item::getTaskDueDate)).containsExactly(d3, d4, d1, d2, d5, d7, d6);
    }


    @Test
    void findItems_filterByRequestType_returnOnlyItemsRelatedToThatRequestType() {
        Long account = accounts.get(0).getId();
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.AER_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account, RequestType.AER, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask2 =
                createRequestTask(user, request2, RequestTaskType.AER_APPLICATION_REVIEW, "t2", request2.getCreationDate());


        Request request3 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE, "t3", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, RequestType.AER, null);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item2 = itemPage.getItems().get(0);
        assertThat(item2.getRequestId()).isEqualTo(request2.getId());
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request2.getId());
        assertEquals(item2.getRequestType(), request2.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account);
        assertTrue(item2.isNew());

    }

    @Test
    void findItems_filterByAccountId_returnOnlyItemsRelatedToThatAccount() {

        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);

        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.AER_APPLICATION_REVIEW));

        Request request1 = createRequest(account1.getId(), RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account1.getId(), RequestType.AER, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask2 =
                createRequestTask(user, request2, RequestTaskType.AER_APPLICATION_REVIEW, "t2", request2.getCreationDate());


        Request request3 = createRequest(account2.getId(), RequestType.AER, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask3 = createRequestTask(user, request3, RequestTaskType.AER_APPLICATION_REVIEW, "t3", request3.getCreationDate());

        Request request4 = createRequest(3L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request1.getCreationDate());


        ItemPage itemPageAccount1 = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, "1");
        ItemPage itemPageAccount2 = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, "2");

        assertEquals(2L, itemPageAccount1.getTotalItems());
        assertEquals(2, itemPageAccount1.getItems().size());

        assertEquals(1L, itemPageAccount2.getTotalItems());
        assertEquals(1, itemPageAccount2.getItems().size());

        assertThat(itemPageAccount1.getItems().stream().map(Item::getTaskId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(requestTask1.getId(), requestTask2.getId());

        assertThat(itemPageAccount2.getItems().stream().map(Item::getTaskId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(requestTask3.getId());
    }

    @Test
    void findItems_filterByAccountName_returnOnlyItemsRelatedToThatAccount() {

        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);

        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.AER_APPLICATION_REVIEW));

        Request request1 = createRequest(account1.getId(), RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account1.getId(), RequestType.AER, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask2 =
                createRequestTask(user, request2, RequestTaskType.AER_APPLICATION_REVIEW, "t2", request2.getCreationDate());


        Request request3 = createRequest(account2.getId(), RequestType.AER, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask3 = createRequestTask(user, request3, RequestTaskType.AER_APPLICATION_REVIEW, "t3", request3.getCreationDate());

        Request request4 = createRequest(3L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request1.getCreationDate());


        ItemPage itemPageAccount1 = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, "Combust");
        ItemPage itemPageAccount2 = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, "refin");

        assertEquals(2L, itemPageAccount1.getTotalItems());
        assertEquals(2, itemPageAccount1.getItems().size());

        assertEquals(1L, itemPageAccount2.getTotalItems());
        assertEquals(1, itemPageAccount2.getItems().size());

        assertThat(itemPageAccount1.getItems().stream().map(Item::getTaskId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(requestTask1.getId(), requestTask2.getId());

        assertThat(itemPageAccount2.getItems().stream().map(Item::getTaskId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(requestTask3.getId());
    }

    @Test
    void findItems_assigned_to_others() {
        Long account = accounts.get(0).getId();
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t2", request2.getCreationDate());


        Request request3 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE, "t3", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask4 = createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        Request request5 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.SCOTLAND, LocalDateTime.now());
        createRequestTask("another user", request5, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request5.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.OTHERS, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, null);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item = itemPage.getItems().get(0);
        assertThat(item.getRequestId()).isEqualTo(request4.getId());
        assertEquals(item.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask4.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item.getRequestId(), request4.getId());
        assertEquals(item.getRequestType(), request4.getType());
        assertEquals(item.getTaskId(), requestTask4.getId());
        assertEquals(item.getTaskType(), requestTask4.getType());
        assertEquals(item.getTaskAssigneeId(), requestTask4.getAssignee());
        assertEquals(item.getTaskDueDate(), requestTask4.getDueDate());
        assertEquals(item.getAccountId(), account);
    }

    @Test
    void findItems_unassigned() {
        Long account = accounts.get(0).getId();
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request1.getCreationDate());

        Request request2 = createRequest(account, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "3", request2.getCreationDate());

        Request request3 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE, "t4", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t6", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0).pageSize(10).build(), ItemOrderBy.NEWEST_FIRST, null, null);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item1 = itemPage.getItems().get(0);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account);
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthorityEnum ca, LocalDateTime creationDate) {
        Request request = Request.builder()
                .id(RandomStringUtils.insecure().next(5))
                .competentAuthority(ca)
                .type(type)
                .status(status)
                .accountId(accountId)
                .creationDate(creationDate)
                .engine(WorkflowEngineType.CAMUNDA)
                .build();

        entityManager.persist(request);

        return request;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
                                          String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask =
                RequestTask.builder()
                        .request(request)
                        .processTaskId(processTaskId)
                        .type(taskType)
                        .assignee(assignee)
                        .startDate(LocalDateTime.now())
                        .dueDate(LocalDate.now().plusMonths(1L))
                        .build();

        entityManager.persist(requestTask);
        requestTask.setStartDate(startDate);

        return requestTask;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
                                          String processTaskId, LocalDateTime startDate, LocalDate dueDate) {
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .type(taskType)
                .assignee(assignee)
                .startDate(LocalDateTime.now())
                .dueDate(dueDate)
                .build();

        entityManager.persist(requestTask);
        requestTask.setStartDate(startDate);

        return requestTask;
    }

    private void createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit =
                RequestTaskVisit.builder()
                        .taskId(taskId)
                        .userId(userId)
                        .build();

        entityManager.persist(requestTaskVisit);
    }

    private List<Account> createAccounts() {
        LegalEntity le = LegalEntity.builder()
                .location(
                        LocationOnShore.builder()
                                .gridReference("grid")
                                .address(
                                        Address.builder()
                                                .city("city")
                                                .country("GR")
                                                .line1("line")
                                                .postcode("postcode")
                                                .build())
                                .build())
                .name("le")
                .status(LegalEntityStatus.ACTIVE)
                .referenceNumber("regNumber")
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);

        InstallationAccount account1 = InstallationAccount.builder()
                .id(1L)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .applicationType(ApplicationType.NEW_PERMIT)
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(1L)
                .status(InstallationAccountStatus.LIVE)
                .location(
                        LocationOnShore.builder()
                                .gridReference("grid")
                                .address(
                                        Address.builder()
                                                .city("city")
                                                .country("GR")
                                                .line1("line")
                                                .postcode("postcode")
                                                .build())
                                .build())
                .name("Combustion")
                .siteName("Combustion")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", 1L))
                .build();


        InstallationAccount account2 = InstallationAccount.builder()
                .id(2L)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .applicationType(ApplicationType.NEW_PERMIT)
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(1L)
                .status(InstallationAccountStatus.LIVE)
                .location(
                        LocationOnShore.builder()
                                .gridReference("grid")
                                .address(
                                        Address.builder()
                                                .city("city")
                                                .country("GR")
                                                .line1("line")
                                                .postcode("postcode")
                                                .build())
                                .build())
                .name("MINERAL oil Refining")
                .siteName("MINERAL oil Refining")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", 2L))
                .build();
        entityManager.persist(account1);
        entityManager.persist(account2);

        return List.of(account1, account2);
    }
}
