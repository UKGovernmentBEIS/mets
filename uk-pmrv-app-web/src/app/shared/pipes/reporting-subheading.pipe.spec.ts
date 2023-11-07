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
        '25319 tCO2 emissions from all international flights\n' +
        '21319 tCO2 emissions from flights with offsetting requirements\n' +
        '4000 tCO2 emissions claimed from CORSIA eligible fuels\n' +
        'Verified as not satisfactory',
    );
  });
});
