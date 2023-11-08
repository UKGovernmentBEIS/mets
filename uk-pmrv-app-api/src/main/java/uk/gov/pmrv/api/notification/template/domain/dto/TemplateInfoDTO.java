package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TemplateInfoDTO {

    private Long id;
    private String name;
    private String workflow;
    private LocalDateTime lastUpdatedDate;

    public TemplateInfoDTO(Long id, String name,String workflow, LocalDateTime lastUpdatedDate) {
        this.id = id;
        this.name = name;
        this.workflow = workflow;
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
