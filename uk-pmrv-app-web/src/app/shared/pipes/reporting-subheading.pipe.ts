import { Pipe, PipeTransform } from '@angular/core';

import { AviationOverallDecisionTypePipe } from '@aviation/shared/pipes/overall-decision-type.pipe';
import { OverallAssessmentTypePipe } from '@shared/pipes/overall-assessment-type.pipe';
import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';

import {
  AerRequestMetadata,
  AviationAerCorsiaRequestMetadata,
  AviationAerRequestMetadata,
  RequestMetadata,
} from 'pmrv-api';

@Pipe({ name: 'reportingSubheading' })
export class ReportingSubheadingPipe implements PipeTransform {
  transform(metadata: RequestMetadata): string {
    switch (metadata.type) {
      case 'AER': {
        const assessmentTypePipe = new OverallAssessmentTypePipe();
        const overallAssessment = assessmentTypePipe.transform((metadata as AerRequestMetadata)?.overallAssessmentType);

        return (metadata as AerRequestMetadata).emissions
          ? format(new BigNumber((metadata as AerRequestMetadata).emissions)) +
              ' tCO2e' +
              (overallAssessment ? ` - ${overallAssessment}` : '')
          : `${overallAssessment}`;
      }
      case 'AVIATION_AER_CORSIA': {
        const overallDecisionTypePipe = new AviationOverallDecisionTypePipe();
        const corsiaMetadata = metadata as AviationAerCorsiaRequestMetadata;
        const overallAssessment = overallDecisionTypePipe.transform(corsiaMetadata?.overallAssessmentType, true);

        return (metadata as AviationAerCorsiaRequestMetadata).emissions
          ? format(new BigNumber(corsiaMetadata.emissions)) +
              ' tCO2 emissions from all international flights\n' +
              format(new BigNumber(corsiaMetadata.totalEmissionsOffsettingFlights)) +
              ' tCO2 emissions from flights with offsetting requirements\n' +
              format(new BigNumber(corsiaMetadata.totalEmissionsClaimedReductions)) +
              ' tCO2 emissions claimed from CORSIA eligible fuels' +
              (overallAssessment ? `\n${overallAssessment}` : '')
          : `${overallAssessment}`;
      }
      case 'AVIATION_AER': {
        const overallDecisionTypePipe = new AviationOverallDecisionTypePipe();
        const overallAssessment = overallDecisionTypePipe.transform(
          (metadata as AviationAerRequestMetadata)?.overallAssessmentType,
        );

        return (metadata as AviationAerRequestMetadata).emissions
          ? format(new BigNumber((metadata as AviationAerRequestMetadata).emissions)) +
              ' tCO2' +
              (overallAssessment ? ` - ${overallAssessment}` : '')
          : `${overallAssessment}`;
      }
      default:
        return '';
    }
  }
}
