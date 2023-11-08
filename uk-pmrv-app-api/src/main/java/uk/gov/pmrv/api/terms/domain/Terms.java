package uk.gov.pmrv.api.terms.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * The persistent class for the terms database table.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "terms")
public class Terms {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "terms_id_generator", sequenceName = "terms_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "terms_id_generator")
    private Long id;

    /**
     * The url for terms and conditions.
     */
    @Column(name = "url")
    @NotBlank
    private String url;

    /**
     * The terms and conditions version.
     */
    @EqualsAndHashCode.Include()
    @Column(name = "version", unique = true)
    @NotNull
    private Short version;

}
