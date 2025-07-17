import { Pipe, PipeTransform } from '@angular/core';

import { PermanentCessation } from 'pmrv-api';

@Pipe({
  name: 'cessationScopePipe',
  standalone: true,
})
export class CessationScopePipePipe implements PipeTransform {
  transform(value: PermanentCessation['cessationScope']): string {
    switch (value) {
      case 'SUB_INSTALLATIONS_ONLY':
        return 'Sub-installations only';
      case 'WHOLE_INSTALLATION':
        return 'Whole installation, including any sub-installations';

      default:
        return '';
    }
  }
}
