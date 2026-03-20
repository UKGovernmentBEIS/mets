import { Pipe, PipeTransform } from '@angular/core';

import { BDRS2GuardQuestions } from 'pmrv-api';

@Pipe({
  name: 'freeAllocationType',
})
export class FreeAllocationTypePipe implements PipeTransform {
  transform(value: BDRS2GuardQuestions['continueApplicationForFreeAllocationType']): string {
    switch (value) {
      case 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT':
        return 'Yes, I hold a GHGE permit and want to continue my application for free allocation as a main scheme participant, or I currently hold HSE status and want to become a main scheme participant from 2027 to 2030';
      case 'CONTINUE_AS_HSE':
        return 'Yes, I currently hold HSE status and want to continue my application for free allocation but remain on the HSE list';
      case 'WITHDRAW':
        return 'No, I want to withdraw my application for free allocation';

      default:
        return '';
    }
  }
}
