package gov.uk.pmrv.keycloak.user.api.model.jpa;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Signature {

    @EqualsAndHashCode.Include()
    @NotBlank
    @Column(name = "signature_uuid")
    private String signatureUuid;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="signature_content")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] signatureContent;

    @NotBlank
    @Column(name = "signature_name")
    private String signatureName;

    @NotNull
    @Positive
    @Column(name = "signature_size")
    private Long signatureSize;

    @NotBlank
    @Column(name = "signature_type")
    private String signatureType;

}
