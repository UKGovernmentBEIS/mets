package uk.gov.pmrv.api.notification.template.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Length;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.notification.template.domain.converter.NotificationTemplateNameConverter;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the notification_template database table.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_template")
@NamedEntityGraph(
    name = "notification-templates-graph",
    attributeNodes = {
        @NamedAttributeNode("text"),
        @NamedAttributeNode("documentTemplates")
    })
@NamedQuery(
    name = NotificationTemplate.NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID,
    query = "select template from NotificationTemplate template "
        + "where template.id = :id "
        + "and template.managed = true")
@SqlResultSetMapping(
    name = NotificationTemplate.NOTIFICATION_TEMPLATE_INFO_DTO_RESULT_MAPPER,
    classes = {
        @ConstructorResult(
            targetClass = TemplateInfoDTO.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "name"),
                @ColumnResult(name = "workflow"),
                @ColumnResult(name = "lastUpdatedDate", type= LocalDateTime.class)
            }
        )})
public class NotificationTemplate {

    public static final String NOTIFICATION_TEMPLATE_INFO_DTO_RESULT_MAPPER = "NotificationTemplateInfoDTOResultMapper";
    public static final String NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID = "NotificationTemplate.findManagedNotificationTemplateById";

    @Id
    @SequenceGenerator(name = "notification_template_id_generator", sequenceName = "notification_template_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_template_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Convert(converter = NotificationTemplateNameConverter.class)
    @Column(name = "name")
    private NotificationTemplateName name;

    @NotNull
    @Column(name = "subject")
    private String subject;

    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "text", length = Length.LOB_DEFAULT)
    private String text;

    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthorityEnum competentAuthority;

    @Column(name = "event_trigger")
    private String eventTrigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "workflow")
    private String workflow;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "is_managed", columnDefinition = "boolean default false")
    private boolean managed;

    @Column(name = "last_updated_date")
    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "notification_template_document_template",
        joinColumns = @JoinColumn(name = "notification_template_id"),
        inverseJoinColumns = @JoinColumn(name = "document_template_id")
    )
    @ToString.Exclude
    private Set<DocumentTemplate> documentTemplates = new HashSet<>();
}
