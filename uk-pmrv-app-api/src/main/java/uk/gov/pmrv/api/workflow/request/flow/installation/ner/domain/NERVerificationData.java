package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NERVerificationData {

    @Valid
    private NERVerificationOpinionStatement opinionStatement;

    @Valid
    private NEROverallVerificationAssessment overallAssessment;
}
