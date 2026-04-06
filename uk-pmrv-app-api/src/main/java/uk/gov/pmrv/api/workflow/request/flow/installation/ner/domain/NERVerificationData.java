package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;


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

    private NERVerificationOpinionStatement opinionStatement;
}
