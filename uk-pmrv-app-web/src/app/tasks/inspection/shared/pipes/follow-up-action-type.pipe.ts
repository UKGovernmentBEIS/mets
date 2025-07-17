import { Pipe, PipeTransform } from '@angular/core';

import { FollowUpAction } from 'pmrv-api';

@Pipe({
  name: 'followUpActionType',
  standalone: true,
})
export class FollowUpActionTypePipe implements PipeTransform {
  transform(followUpActionType: FollowUpAction['followUpActionType']): string {
    switch (followUpActionType) {
      case 'MISSTATEMENT':
        return 'Misstatement';
      case 'NON_CONFORMITY':
        return 'Non-conformity';
      case 'NON_COMPLIANCE':
        return 'Non-compliance';
      case 'RECOMMENDED_IMPROVEMENT':
        return 'Recommended improvement';
      case 'UNRESOLVED_ISSUE_FROM_PREVIOUS_AUDIT':
        return 'Unresolved issue from previous audit';
      default:
        return '';
    }
  }
}
