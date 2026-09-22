package uk.gov.pmrv.api.mireport.userdefined;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedEntity;
import uk.gov.netz.api.mireport.userdefined.category.MiReportUserDefinedCategoryEntity;
import uk.gov.netz.api.mireport.userdefined.favourite.MiReportUserDefinedFavouriteEntity;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PmrvMiReportUserDefinedRepositoryIT extends AbstractContainerBaseTest {

    private static final CompetentAuthorityEnum CA = CompetentAuthorityEnum.ENGLAND;
    private static final CompetentAuthorityEnum OTHER_CA = CompetentAuthorityEnum.WALES;
    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";

    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private PmrvMiReportUserDefinedRepository repository;

    @Autowired
    private TestEntityManager em;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "lastUpdatedOn");

    @Test
    void findAllByCompetentAuthorityAndFilters_filtersByAccountType() {
        Long installation =
                persistReport("Installation report", "desc", CA, AccountType.INSTALLATION, NOW);
        persistReport("Aviation report", "desc", CA, AccountType.AVIATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(installation);
    }

    @Test
    void findAllByCompetentAuthorityAndFilters_excludesReportsWithoutAccountTypeRow() {
        Long report = persistReportWithoutAccountType("No account type", "desc", CA, NOW, Set.of());
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .doesNotContain(report);
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void findAllByCompetentAuthorityAndFilters_nullCategory_ignoresCategoryFilter() {
        MiReportUserDefinedCategoryEntity category = persistCategory("Financial");
        Long withCategory = persistReport("With category", "desc", CA, AccountType.INSTALLATION, NOW,
                Set.of(category));
        Long withoutCategory =
                persistReport("Without category", "desc", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactlyInAnyOrder(withCategory, withoutCategory);
    }

    @Test
    void findAllByCompetentAuthorityAndFilters_withCategory_returnsOnlyMatching() {
        MiReportUserDefinedCategoryEntity financial = persistCategory("Financial");
        MiReportUserDefinedCategoryEntity compliance = persistCategory("Compliance");
        Long financialReport = persistReport("Financial report", "desc", CA,
                AccountType.INSTALLATION, NOW, Set.of(financial));
        persistReport("Compliance report", "desc", CA, AccountType.INSTALLATION, NOW,
                Set.of(compliance));
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, financial.getId(), null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(financialReport);
    }

    @Test
    void findAllByAccountTypeAndFilters_reportWithMultipleCategories_notDuplicated() {
        MiReportUserDefinedCategoryEntity financial = persistCategory("Financial");
        MiReportUserDefinedCategoryEntity compliance = persistCategory("Compliance");
        Long report = persistReport("Multi category", "desc", CA, AccountType.INSTALLATION, NOW,
                Set.of(financial, compliance));
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(report);
    }

    @Test
    void findAllByAccountTypeAndFilters_nullTerm_ignoresTermFilter() {
        Long a = persistReport("Alpha", "desc", CA, AccountType.INSTALLATION, NOW);
        Long b = persistReport("Beta", "desc", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactlyInAnyOrder(a, b);
    }

    @Test
    void findAllByAccountTypeAndFilters_termMatchesReportNameCaseInsensitive() {
        Long match =
                persistReport("Emissions Summary", "some description", CA, AccountType.INSTALLATION, NOW);
        persistReport("Compliance list", "some description", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, "%emissions%", null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(match);
    }

    @Test
    void findAllByAccountTypeAndFilters_termMatchesDescription() {
        Long match = persistReport("Report A", "Contains keyword targeted", CA,
                AccountType.INSTALLATION, NOW);
        persistReport("Report B", "nothing relevant", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, "%targeted%", null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(match);
    }

    @Test
    void findAllByAccountTypeAndFilters_termMatchesNothing_returnsEmpty() {
        persistReport("Report A", "desc", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, "%nomatch%", null, PageRequest.of(0, 10, sort));

        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void findAllByAccountTypeAndFilters_nullUserId_ignoresFavourites() {
        Long favourited = persistReport("Favourited", "desc", CA, AccountType.INSTALLATION, NOW);
        Long notFavourited =
                persistReport("Not favourited", "desc", CA, AccountType.INSTALLATION, NOW);
        persistFavourite(favourited, USER_ID);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactlyInAnyOrder(favourited, notFavourited);
    }

    @Test
    void findAllByAccountTypeAndFilters_withUserId_returnsOnlyUsersFavourites() {
        Long myFavourite = persistReport("Mine", "desc", CA, AccountType.INSTALLATION, NOW);
        Long othersFavourite = persistReport("Theirs", "desc", CA, AccountType.INSTALLATION, NOW);
        persistReport("Nobody's", "desc", CA, AccountType.INSTALLATION, NOW);
        persistFavourite(myFavourite, USER_ID);
        persistFavourite(othersFavourite, OTHER_USER_ID);
        flushAndClear();

        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, USER_ID, PageRequest.of(0, 10, sort));

        assertThat(result.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(myFavourite);
    }

    @Test
    void findAllByAccountTypeAndFilters_pagingAndSortingByLastUpdatedOnDesc() {
        Long oldest = persistReport("Oldest", "desc", CA, AccountType.INSTALLATION,
                NOW.minusDays(2));
        Long middle = persistReport("Middle", "desc", CA, AccountType.INSTALLATION,
                NOW.minusDays(1));
        Long newest = persistReport("Newest", "desc", CA, AccountType.INSTALLATION, NOW);
        flushAndClear();

        Page<MiReportUserDefinedEntity> page0 = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 2, sort));
        assertThat(page0.getTotalElements()).isEqualTo(3);
        assertThat(page0.getTotalPages()).isEqualTo(2);
        assertThat(page0.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(newest, middle);

        Page<MiReportUserDefinedEntity> page1 = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(1, 2, sort));
        assertThat(page1.getContent()).extracting(MiReportUserDefinedEntity::getId)
                .containsExactly(oldest);
    }


    @Test
    void findAllByAccountTypeAndFilters_noData_returnsEmpty() {
        Page<MiReportUserDefinedEntity> result = repository.findAllByAccountTypeAndFilters(
                AccountType.INSTALLATION, null, null, null, PageRequest.of(0, 10, sort));

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }


    private Long persistReport(String reportName, String description, CompetentAuthorityEnum ca,
                               AccountType accountType, LocalDateTime lastUpdatedOn) {
        return persistReport(reportName, description, ca, accountType, lastUpdatedOn, Set.of());
    }

    private Long persistReport(String reportName, String description, CompetentAuthorityEnum ca,
                               AccountType accountType, LocalDateTime lastUpdatedOn,
                               Set<MiReportUserDefinedCategoryEntity> categories) {
        Long id = persistReportWithoutAccountType(reportName, description, ca, lastUpdatedOn,
                categories);
        em.persist(MiReportUserDefinedAccountType.builder()
                .miReportId(id)
                .accountType(accountType)
                .build());
        return id;
    }

    private Long persistReportWithoutAccountType(String reportName, String description,
                                                 CompetentAuthorityEnum ca,
                                                 LocalDateTime lastUpdatedOn,
                                                 Set<MiReportUserDefinedCategoryEntity> categories) {
        MiReportUserDefinedEntity entity = MiReportUserDefinedEntity.builder()
                .reportName(reportName)
                .description(description)
                .queryDefinition("select 1")
                .competentAuthority(ca)
                .createdBy("tester")
                .lastUpdatedOn(lastUpdatedOn)
                .categories(categories)
                .build();
        return em.persist(entity).getId();
    }

    private MiReportUserDefinedCategoryEntity persistCategory(String name) {
        return em.persist(MiReportUserDefinedCategoryEntity.builder()
                .name(name)
                .enabled(true)
                .build());
    }

    private void persistFavourite(Long miReportId, String userId) {
        em.persist(MiReportUserDefinedFavouriteEntity.builder()
                .miReportId(miReportId)
                .userId(userId)
                .build());
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
