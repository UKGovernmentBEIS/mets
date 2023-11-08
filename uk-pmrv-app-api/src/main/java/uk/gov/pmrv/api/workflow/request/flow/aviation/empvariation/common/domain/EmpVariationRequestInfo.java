package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpVariationRequestInfo {

    private String id; //request id
    private LocalDateTime submissionDate; //request submission date
    private LocalDateTime endDate; //request end date
    private EmpVariationRequestMetadata metadata;
}
