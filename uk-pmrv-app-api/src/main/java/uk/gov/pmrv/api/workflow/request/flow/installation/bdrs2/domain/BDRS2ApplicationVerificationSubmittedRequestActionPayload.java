package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BDRS2ApplicationVerificationSubmittedRequestActionPayload extends BDRS2ApplicationSubmittedRequestActionPayload {
}
