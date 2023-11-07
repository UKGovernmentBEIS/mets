import { AviationAerMaterialityLevel } from 'pmrv-api';

export const aviationAerUkeEtsForAccreditedVerifiers: AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'] =
  [
    'UK_ETS_ACCREDITED_VERIFIERS_SI_2020_1265',
    'UK_ETS_ACCREDITED_VERIFIERS_EU_2018_2067',
    'UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14065_2020',
    'UK_ETS_ACCREDITED_VERIFIERS_EN_ISO_14064_3_2019',
    'UK_ETS_ACCREDITED_VERIFIERS_IAF_MD_6_2014',
    'UK_ETS_ACCREDITED_VERIFIERS_AUTHORITY_GUIDANCE',
    'UK_ETS_ACCREDITED_VERIFIERS_EUROPEAN_COMMISSION_GUIDANCE',
  ];

export const aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders: AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'] =
  ['UK_ETS_ACCREDITED_VERIFIERS_ISAE_3000', 'UK_ETS_ACCREDITED_VERIFIERS_ISAE_3410'];

export const aviationAerUkeEtsRulesUkEts: AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'] = [
  'UK_ETS_RULES_ORDER_2020_1265',
  'UK_ETS_RULES_ORDER_2020_1557',
  'UK_ETS_RULES_EU_2018_2067',
  'UK_ETS_RULES_EU_2018_2066',
  'UK_ETS_RULES_EUROPEAN_COMMISSION_GUIDANCE',
];

export const aviationAerUkeEtsRulesUkEtsOther: AviationAerMaterialityLevel['accreditationReferenceDocumentTypes'] = [
  'OTHER',
];
