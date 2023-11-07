import { Pipe, PipeTransform } from '@angular/core';

import { MaterialityLevel } from 'pmrv-api';

@Pipe({ name: 'accreditationReferenceDocumentName' })
export class AccreditationReferenceDocumentNamePipe implements PipeTransform {
  transform(documentType?: MaterialityLevel['accreditationReferenceDocumentTypes'][0]): string {
    switch (documentType) {
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_SI_2020_1265':
        return 'The Greenhouse Gas Emissions Trading Scheme Order 2020 (SI 2020/1265) as amended by The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020 (SI 2020/1557) (the UK ETS Order)';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067':
        return 'Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_ISO_14065_2020':
        return 'EN ISO 14065:2020 Requirements for greenhouse gas validation and verification bodies for use in accreditation or other forms of recognition';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_ISO_14064_3_2019':
        return 'EN ISO 14064-3:2019 Specification with guidance for the validation and verification of GHG assertions';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_MD_6_2014':
        return 'IAF MD 6:2014 International Accreditation Forum (IAF) Mandatory Document for the Application of ISO 14065:2013 (Issue 2, March 2014)';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_GUIDANCE_AUTHORITY_MONITORING_REPORTING_REGULATION':
        return 'Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_GUIDANCE_EUROPEAN_COMISSION_UK_ETS_ORDER':
        return 'Guidance developed by the European Commission in relation to the Verification Regulation, for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order';

      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3000':
        return 'International Standard on Assurance Engagements 3000 : Assurance Engagements other than Audits or Reviews of Historical Information, issued by the International Auditing and Assurance Standards Board';
      case 'UK_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3410':
        return 'International Standard on Assurance Engagements 3410 : Assurance Engagements on Greenhouse Gas Statements, issued by the International Auditing and Assurance Standards Board';

      case 'UK_ETS_VERIFICATION_RULE_ORDER_2020_1265':
        return 'The UK ETS Order 2020/1265 (The Greenhouse Gas Emissions Trading Scheme Order 2020)';
      case 'UK_ETS_VERIFICATION_RULE_ORDER_2020_1557':
        return 'The UK ETS Order 2020/1557 (The Greenhouse Gas Emissions Trading Scheme (Amendment) Order 2020)';
      case 'UK_ETS_VERIFICATION_RULE_EU_REGULATION_2018_2067':
        return 'Commission Implementing Regulation (EU) 2018/2067 on the verification of data and on the accreditation of verifiers (the Verification Regulation), as amended by Schedule 5 of the UK ETS Order 2020';
      case 'UK_ETS_VERIFICATION_RULE_EU_REGULATION_2018_2066':
        return 'Commission Implementing Regulation (EU) 2018/2066 on monitoring and reporting of greenhouse gas emissions, as amended by Schedule 4 of the UK ETS Order 2020';
      case 'UK_ETS_VERIFICATION_RULE_GUIDANCE_EUROPEAN_COMISSION_UK_ETS_ORDER':
        return 'Guidance developed by the European Commission in relation to the Verification Regulation for the purposes of the EU ETS, in so far as they can be applied to the implementation of the UK ETS Order';

      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_REGULATION_EU_2018_2067':
        return 'EU Regulation EU no.  2018/2067 on verification of data and the accreditation of verifiers pursuant to Directive 2003/87/EC….. (AVR)';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_ISO_14065_2020':
        return 'EN ISO 14065:2020 General principles and requirements for bodies validating and verifying environmental information';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_ISO_14064_3_2019':
        return 'EN ISO 14064-3:2019 Specification with guidance for the validation and verification of GHG assertions';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_MD_6_2014':
        return 'IAF MD 6:2014 International Accreditation Forum (IAF) Mandatory Document for the Application of ISO 14065:2013 (Issue 2, March 2014)';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_GUIDANCE_EUROPEAN_COMISSION_FOR_VER_ACCREDIATION':
        return 'Guidance developed by European Commission Services on verification and accreditation';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_EA_6_03':
        return 'EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive';

      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3000':
        return 'International Standard on Assurance Engagements 3000 : Assurance Engagements other than Audits or Reviews of Historical Information, issued by the International Auditing and Assurance Standards Board.';
      case 'EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_AND_PROVIDERS_3410':
        return 'International Standard on Assurance Engagements 3410 : Assurance Engagements on Greenhouse Gas Statements, issued by the International Auditing and Assurance Standards Board.';

      case 'EU_ETS_VERIFICATION_RULE_EU_REGULATION_2018_2066':
        return 'EU Regulation 2018/2066 on the Monitoring and Reporting of Greenhouse Gas Emissions, pursuant to Directive 2003/87/EC (MRR)';
      case 'EU_ETS_VERIFICATION_RULE_GUIDANCE_EUROPEAN_COMISSION_MONITORING_REPORTING_REGULATION':
        return 'EU Guidance developed by the European Commission Services to support the harmonised interpretation of the Monitoring and Reporting Regulation';
      case 'EU_ETS_VERIFICATION_RULE_GUIDANCE_EUROPEAN_COMISSION_ACCREDITATION_VER_REGULATION':
        return 'EU guidance developed by the European Commission Services to support the harmonised interpretation of the Accreditation and Verification Regulation';

      case 'OTHER':
        return 'Add your own reference';
      default:
        return '';
    }
  }
}
