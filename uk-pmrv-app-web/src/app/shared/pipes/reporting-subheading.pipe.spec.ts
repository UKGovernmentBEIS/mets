import { RequestMetadata } from 'pmrv-api';

import { ReportingSubheadingPipe } from './reporting-subheading.pipe';

describe('ReportingSubheadingPipe', () => {
  let pipe: ReportingSubheadingPipe;

  beforeEach(() => (pipe = new ReportingSubheadingPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform metadata info', () => {
    const metadataVerified = {
      type: 'AER',
      year: '2021',
      emissions: '25319',
      overallAssessmentType: 'VERIFIED_WITH_COMMENTS',
    };
    expect(pipe.transform(metadataVerified as RequestMetadata)).toEqual('25319 tCO2e - Verified with comments');

    const metadataNotVerified = {
      type: 'AER',
      year: '2021',
      emissions: '25319',
      overallAssessmentType: 'NOT_VERIFIED',
    };
    expect(pipe.transform(metadataNotVerified as RequestMetadata)).toEqual('25319 tCO2e - Not verified');

    const metadataVerifiedSatisfactory = {
      type: 'AER',
      year: '2021',
      emissions: '25319',
      overallAssessmentType: 'VERIFIED_AS_SATISFACTORY',
    };
    expect(pipe.transform(metadataVerifiedSatisfactory as RequestMetadata)).toEqual(
      '25319 tCO2e - Verified as satisfactory',
    );

    const metadataNoEmissions = {
      type: 'AER',
    };
    expect(pipe.transform(metadataNoEmissions as RequestMetadata)).toEqual('');

    const metadataNotVerifiedUkets = {
      type: 'AVIATION_AER',
      year: '2021',
      emissions: '25319',
      overallAssessmentType: 'NOT_VERIFIED',
    };
    expect(pipe.transform(metadataNotVerifiedUkets as RequestMetadata)).toEqual('25319 tCO2 - Not verified');

    const metadataNotVerifiedCorsia = {
      type: 'AVIATION_AER_CORSIA',
      year: '2021',
      emissions: '25319',
      totalEmissionsOffsettingFlights: '21319',
      totalEmissionsClaimedReductions: '4000',
      overallAssessmentType: 'NOT_VERIFIED',
    };
    expect(pipe.transform(metadataNotVerifiedCorsia as RequestMetadata)).toEqual(
      '' +
        '25319 tCO2 emissions from all international flights\n ' +
        '21319 tCO2 emissions from flights with offsetting requirements\n ' +
        '4000 tCO2 emissions claimed from CORSIA eligible fuels\n' +
        'Verified as not satisfactory',
    );

    const annualOffsettingMetadata = {
      type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING',
      year: '2021',
      calculatedAnnualOffsetting: '25319',
    };
    expect(pipe.transform(annualOffsettingMetadata as RequestMetadata)).toEqual(
      '25319 tCO2 - annual offsetting requirements',
    );

    const annualOffsettingMetadata2 = {
      type: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING',
      year: '2021',
    };
    expect(pipe.transform(annualOffsettingMetadata2 as RequestMetadata)).toEqual('');

    const threeYearPeriodOffsettingMetadata = {
      type: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING',
      year: '2023',
      years: ['2021', '2022', '2023'],
      periodOffsettingRequirements: 888,
      operatorHaveOffsettingRequirements: true,
    };
    expect(pipe.transform(threeYearPeriodOffsettingMetadata as RequestMetadata)).toEqual(
      '888 tCO2 - 2021-2023 period offsetting requirements',
    );
    expect(
      pipe.transform({
        ...threeYearPeriodOffsettingMetadata,
        operatorHaveOffsettingRequirements: false,
      } as RequestMetadata),
    ).toEqual('0 tCO2 - 2021-2023 period offsetting requirements');

    const bdrMetadata = {
      type: 'BDR',
      year: '2021',
      overallAssessmentType: 'VERIFIED_WITH_COMMENTS',
    };
    expect(pipe.transform(bdrMetadata as RequestMetadata)).toEqual('Verified with comments');

    const metadataDoeCorsia = {
      type: 'AVIATION_DOE_CORSIA',
      year: '2021',
      emissions: '25319',
      totalEmissionsOffsettingFlights: '21319',
      totalEmissionsClaimedReductions: '4000',
    };
    expect(pipe.transform(metadataDoeCorsia as RequestMetadata)).toEqual(
      '' +
        '25319 tCO2 emissions from all international flights\n ' +
        '21319 tCO2 emissions from flights with offsetting requirements\n ' +
        '4000 tCO2 emissions claimed from CORSIA eligible fuels',
    );
  });
});
