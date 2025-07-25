package uk.gov.pmrv.api.workflow.request.application.item.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestTaskVisitPK implements Serializable {

    private Long taskId;

    private String userId;
}
