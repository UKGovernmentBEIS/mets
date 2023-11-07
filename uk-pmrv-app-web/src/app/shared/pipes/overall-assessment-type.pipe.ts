import { Pipe, PipeTransform } from '@angular/core';

import { OverallAssessment } from 'pmrv-api';

@Pipe({ name: 'overallAssessmentType' })
export class OverallAssessmentTypePipe implements PipeTransform {
  transform(assessmentType: OverallAssessment['type']): string {
    switch (assessmentType) {
      case 'NOT_VERIFIED':
        return 'Not verified';
      case 'VERIFIED_AS_SATISFACTORY':
        return 'Verified as satisfactory';
      case 'VERIFIED_WITH_COMMENTS':
        return 'Verified with comments';
      default:
        return '';
    }
  }
}
