import { Pipe, PipeTransform } from '@angular/core';

import { EmpDetermination } from '@aviation/request-task/emp/shared/util/emp.util';

@Pipe({
  name: 'empReviewDeterminationType',
  pure: true,
  standalone: true,
})
export class EmpReviewDeterminationTypePipe implements PipeTransform {
  transform(type: EmpDetermination['type']): string | null {
    let val: string = null;
    switch (type) {
      case 'APPROVED':
        val = 'Approve';
        break;
      case 'REJECTED':
        val = 'Reject';
        break;
      case 'DEEMED_WITHDRAWN':
        val = 'Withdraw';
        break;
      default:
        break;
    }
    return val;
  }
}
