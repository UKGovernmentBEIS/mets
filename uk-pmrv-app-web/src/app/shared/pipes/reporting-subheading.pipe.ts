import { Pipe, PipeTransform } from '@angular/core';

import { OverallAssessmentTypePipe } from '@shared/pipes/overall-assessment-type.pipe';
import { OverallDecisionTypePipe } from '@shared/pipes/overall-decision-type.pipe';
import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';

import {
  AerRequestMetadata,
  AviationAerCorsia3YearPeriodOffsettingRequestMetadata,
  AviationAerCorsiaRequestMetadata,
  AviationAerRequestMetadata,
  AviationDoECorsiaRequestMetadata,
  BDRRequestMetadata,
  RequestMetadata,
} from 'pmrv-api';

@Pipe({ name: 'reportingSubheading' })
export class ReportingSubheadingPipe implements PipeTransform {
  private readonly assessmentTypePipe = new OverallAssessmentTypePipe();
  private readonly overallDecisionTypePipe = new OverallDecisionTypePipe();

  transform(metadata: RequestMetadata): string {
    switch (metadata.type) {
      case 'AER': {
        const overallAssessment = this.assessmentTypePipe.transform(
          (metadata as AerRequestMetadata)?.overallAssessmentType,
        );

        return (metadata as AerRequestMetadata).emissions
          ? format(new BigNumber((metadata as AerRequestMetadata).emissions)) +
              ' tCO2e' +
              (overallAssessment ? ` - ${overallAssessment}` : '')
          : `${overallAssessment}`;
      }
      case 'AVIATION_AER_CORSIA': {
        const corsiaMetadata = metadata as AviationAerCorsiaRequestMetadata;
        const overallAssessment = this.overallDecisionTypePipe.transform(corsiaMetadata?.overallAssessmentType, true);

        return (metadata as AviationAerCorsiaRequestMetadata).emissions
          ? format(new BigNumber(corsiaMetadata.emissions)) +
              ' tCO2 emissions from all international flights\n ' +
              format(new BigNumber(corsiaMetadata.totalEmissionsOffsettingFlights)) +
              ' tCO2 emissions from flights with offsetting requirements\n ' +
              format(new BigNumber(corsiaMetadata.totalEmissionsClaimedReductions)) +
              ' tCO2 emissions claimed from CORSIA eligible fuels' +
              (overallAssessment ? `\n${overallAssessment}` : '')
          : `No emissions reported \n${overallAssessment}`;
      }
      case 'AVIATION_AER': {
        const overallAssessment = this.overallDecisionTypePipe.transform(
          (metadata as AviationAerRequestMetadata)?.overallAssessmentType,
        );

        return (metadata as AviationAerRequestMetadata).emissions
          ? format(new BigNumber((metadata as AviationAerRequestMetadata).emissions)) +
              ' tCO2' +
              (overallAssessment ? ` - ${overallAssessment}` : '')
          : `${overallAssessment}`;
      }
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING': {
        const calculatedAnnualOffsetting = metadata['calculatedAnnualOffsetting'];

        return calculatedAnnualOffsetting !== undefined
          ? format(new BigNumber(calculatedAnnualOffsetting)) + ' tCO2 - annual offsetting requirements'
          : '';
      }
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING': {
        const { years, periodOffsettingRequirements, operatorHaveOffsettingRequirements } =
          metadata as AviationAerCorsia3YearPeriodOffsettingRequestMetadata;

        return periodOffsettingRequirements !== undefined
          ? `${operatorHaveOffsettingRequirements ? format(new BigNumber(periodOffsettingRequirements)) : 0} tCO2 - ${years[0]}-${years[years.length - 1]} period offsetting requirements`
          : '';
      }
      case 'BDR':
        return this.assessmentTypePipe.transform((metadata as BDRRequestMetadata)?.overallAssessmentType);

      case 'AVIATION_DOE_CORSIA': {
        const corsiaMetadata = metadata as AviationDoECorsiaRequestMetadata;

        const emissionsString = (metadata as AviationAerCorsiaRequestMetadata).emissions
          ? format(new BigNumber(corsiaMetadata.emissions)) + ' tCO2 emissions from all international flights\n '
          : '';
        const offsettingString = (metadata as AviationAerCorsiaRequestMetadata).totalEmissionsOffsettingFlights
          ? format(new BigNumber(corsiaMetadata.totalEmissionsOffsettingFlights)) +
            ' tCO2 emissions from flights with offsetting requirements\n '
          : '';
        const claimedReductionString = (metadata as AviationAerCorsiaRequestMetadata).totalEmissionsClaimedReductions
          ? format(new BigNumber(corsiaMetadata.totalEmissionsClaimedReductions)) +
            ' tCO2 emissions claimed from CORSIA eligible fuels'
          : '';

        return (metadata as AviationAerCorsiaRequestMetadata).emissions ||
          (metadata as AviationAerCorsiaRequestMetadata).totalEmissionsOffsettingFlights ||
          (metadata as AviationAerCorsiaRequestMetadata).totalEmissionsClaimedReductions
          ? emissionsString + offsettingString + claimedReductionString
          : 'No emissions reported';
      }
      default:
        return '';
    }
  }
}
