package uk.gov.pmrv.api.notification.template.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class DocumentTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private DocumentTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByTypeAndCompetentAuthorityAndAccountType() {
        DocumentTemplateType documentTemplateType = DocumentTemplateType.IN_RFI;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;

        DocumentTemplate documentTemplate = DocumentTemplate.builder()
                .type(documentTemplateType)
                .competentAuthority(competentAuthority)
                .fileDocumentTemplateId(1L)
                .name("doc template name")
                .accountType(accountType)
                .workflow("workflow")
                .build();
        
        entityManager.persist(documentTemplate);

        flushAndClear();
        
        Optional<DocumentTemplate> resultOpt =
            repo.findByTypeAndCompetentAuthorityAndAccountType(documentTemplateType, competentAuthority, accountType);
        assertThat(resultOpt).isNotEmpty();
        assertThat(resultOpt.get().getName()).isEqualTo("doc template name");
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
