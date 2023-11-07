import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerMaterialityLevel } from 'pmrv-api';

@Pipe({
  name: 'accreditationReferenceDocumentName',
  standalone: true,
})
export class AccreditationReferenceDocumentNamePipe implements PipeTransform {
  transform(documentType?: AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'][0]): string {
    switch (documentType) {
      // For accredited verifiers
      case 'UK_ETS_ACCREDITED_VERIFIERS_SI_2020_1265':
        return 'The Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) as amended by The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020 (SI 2020/1557) (the UK ETS Order)';

      case 'UK_ETS_ACCREDITED_VERIFIERS_EU_2018_2067':
        return 'Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020';

      case 'UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14065_2020':
        return 'EN ISO 14065:2020 Requirements for greenhouse gas validation and verification bodies for use in accreditation or other forms of recognition';

      case 'UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14064_3_2019':
        return 'EN ISO 14064-3:2019 Specification with guidance for the validation and verification of GHG assertions';

      case 'UK_ETS_ACCREDITED_VERIFIERS_IAF_MD_6_2014':
        return 'IAF MD 6:2014 International Accreditation Forum (IAF) Mandatory Document for the Application of ISO 14065:2013 (Issue 2, March 2014)';

      case 'UK_ETS_ACCREDITED_VERIFIERS_AUTHORITY_GUIDANCE':
        return 'Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation';

      case 'UK_ETS_ACCREDITED_VERIFIERS_EUROPEAN_COMMISSION_GUIDANCE':
        return 'Guidance developed by the European Commission in relation to the Verification Regulation, for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order';

      // For accredited verifiers that are also financial assurance providers
      case 'UK_ETS_ACCREDITED_VERIFIERS_ISAE_3000':
        return 'International Standard on Assurance Engagements 3000 : Assurance Engagements other than Audits or Reviews of Historical Information, issued by the International Auditing and Assurance Standards Board';

      case 'UK_ETS_ACCREDITED_VERIFIERS_ISAE_3410':
        return 'International Standard on Assurance Engagements 3410 : Assurance Engagements on Greenhouse Gas Statements, issued by the International Auditing and Assurance Standards Board';

      // Rules of the UK ETS
      case 'UK_ETS_RULES_ORDER_2020_1265':
        return 'The UK ETS Order 2020/1265 (The Greenhouse Gas Emissions Trading Scheme Order 2020)';

      case 'UK_ETS_RULES_ORDER_2020_1557':
        return 'The UK ETS Order 2020/1557 (The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020)';

      case 'UK_ETS_RULES_EU_2018_2067':
        return 'Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020';

      case 'UK_ETS_RULES_EU_2018_2066':
        return 'Commission Implementing Regulation (EU) 2018/2066 on monitoring and reporting of greenhouse gas emissions, as amended by Schedule 4 of the UK ETS Order 2020';

      case 'UK_ETS_RULES_EUROPEAN_COMMISSION_GUIDANCE':
        return 'Guidance developed by the European Commission in relation to the Verification Regulation for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order';

      case 'OTHER':
        return 'Add your own reference';

      default:
        return '';
    }
  }
}
