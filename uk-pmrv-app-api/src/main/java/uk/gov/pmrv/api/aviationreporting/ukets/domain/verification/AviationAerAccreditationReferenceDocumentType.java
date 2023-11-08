package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationAerAccreditationReferenceDocumentType {

    UK_ETS_ACCREDITED_VERIFIERS_SI_2020_1265("The Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) as amended by The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020 (SI 2020/1557) (the UK ETS Order)"),
    UK_ETS_ACCREDITED_VERIFIERS_EU_2018_2067("Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020"),
    UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14065_2020("EN ISO 14065:2020 Requirements for greenhouse gas validation and verification bodies for use in accreditation or other forms of recognition"),
    UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14064_3_2019("EN ISO 14064-3:2019 Specification with guidance for the validation and verification of GHG assertions"),
    UK_ETS_ACCREDITED_VERIFIERS_IAF_MD_6_2014("IAF MD 6:2014 International Accreditation Forum (IAF) Mandatory Document for the Application of ISO 14065:2013 (Issue 2, March 2014)"),
    UK_ETS_ACCREDITED_VERIFIERS_AUTHORITY_GUIDANCE("Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation"),
    UK_ETS_ACCREDITED_VERIFIERS_EUROPEAN_COMMISSION_GUIDANCE("Guidance developed by the European Commission in relation to the Verification Regulation, for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order"),

    UK_ETS_ACCREDITED_VERIFIERS_ISAE_3000("International Standard on Assurance Engagements 3000 : Assurance Engagements other than Audits or Reviews of Historical Information, issued by the International Auditing and Assurance Standards Board"),
    UK_ETS_ACCREDITED_VERIFIERS_ISAE_3410("International Standard on Assurance Engagements 3410 : Assurance Engagements on Greenhouse Gas Statements, issued by the International Auditing and Assurance Standards Board"),

    UK_ETS_RULES_ORDER_2020_1265("The UK ETS Order 2020/1265 (The Greenhouse Gas Emissions Trading Scheme Order 2020)"),
    UK_ETS_RULES_ORDER_2020_1557("The UK ETS Order 2020/1557 (The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020)"),
    UK_ETS_RULES_EU_2018_2067("Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020"),
    UK_ETS_RULES_EU_2018_2066("Commission Implementing Regulation (EU) 2018/2066 on monitoring and reporting of greenhouse gas emissions, as amended by Schedule 4 of the UK ETS Order 2020"),
    UK_ETS_RULES_EUROPEAN_COMMISSION_GUIDANCE("Guidance developed by the European Commission in relation to the Verification Regulation for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order"),

    OTHER("Other reference");

    private final String description;
}
