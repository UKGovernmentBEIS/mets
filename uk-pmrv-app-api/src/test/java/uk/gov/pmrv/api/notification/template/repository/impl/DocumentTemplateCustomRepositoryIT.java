package uk.gov.pmrv.api.notification.template.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class DocumentTemplateCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private DocumentTemplateCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority_without_search_term() {
        String workflow = "workflow";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            competentAuthority, workflow, accountType, 1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            CompetentAuthorityEnum.WALES, workflow, accountType, 2L);
        createDocumentTemplate(DocumentTemplateType.IN_RDE, "IN Request for Determination Extension",
            competentAuthority, workflow, AccountType.AVIATION,3L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .competentAuthority(competentAuthority)
            .accountType(accountType)
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        TemplateSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }

    @Test
    void findByCompetentAuthority_with_search_term_in_workflow() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            competentAuthority, "RFI sub-process", accountType,1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            CompetentAuthorityEnum.WALES, "RFI sub-process", accountType, 2L);
        createDocumentTemplate(DocumentTemplateType.IN_RDE, "IN Request for Determination Extension",
            competentAuthority, "RDE sub-process", AccountType.AVIATION,3L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .competentAuthority(competentAuthority)
            .accountType(accountType)
            .term("process")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        TemplateSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }
    
    @Test
    void findByCompetentAuthority_with_search_term_in_name() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            competentAuthority, "RFI sub-process", accountType, 1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information",
            CompetentAuthorityEnum.WALES, "RFI sub-process", accountType, 2L);
        createDocumentTemplate(DocumentTemplateType.IN_RDE, "IN Request for Determination Extension",
            competentAuthority, "RDE sub-process", AccountType.AVIATION,3L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .competentAuthority(competentAuthority)
            .accountType(accountType)
            .term("Request")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        TemplateSearchResults searchResults = repo.findBySearchCriteria(searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }

    private DocumentTemplate createDocumentTemplate(DocumentTemplateType type, String name, CompetentAuthorityEnum ca,
                                                    String workflow, AccountType accountType, Long fileDocumentTemplateId) {
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .type(type)
            .name(name)
            .competentAuthority(ca)
            .workflow(workflow)
            .accountType(accountType)
            .lastUpdatedDate(LocalDateTime.now())
            .fileDocumentTemplateId(fileDocumentTemplateId)
            .build();

        entityManager.persist(documentTemplate);

        return documentTemplate;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
