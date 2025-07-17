import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerVerificationDecision, BDRVerifiedWithCommentsOverallVerificationAssessment } from 'pmrv-api';

@Pipe({ name: 'verificationDecisionType', standalone: true })
export class OverallDecisionTypePipe implements PipeTransform {
  transform(
    assessmentType:
      | AviationAerVerificationDecision['type']
      | BDRVerifiedWithCommentsOverallVerificationAssessment['type'],
    isCorsia?: boolean,
  ): string {
    switch (assessmentType) {
      case 'NOT_VERIFIED':
        return isCorsia ? 'Verified as not satisfactory' : 'Not verified';
      case 'VERIFIED_AS_SATISFACTORY':
        return 'Verified as satisfactory';
      case 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS':
      case 'VERIFIED_WITH_COMMENTS':
        return 'Verified as satisfactory with comments';

      default:
        return '';
    }
  }
}
