import { Pipe, PipeTransform } from '@angular/core';

import { AerAmendGroup } from '../../core/aer.amend.types';

@Pipe({
  name: 'amendHeading',
})
export class AmendHeadingPipe implements PipeTransform {
  transform(value: AerAmendGroup): string {
    switch (value) {
      case 'INSTALLATION_DETAILS':
        return 'installation details';

      case 'FUELS_AND_EQUIPMENT':
        return 'fuels and equipment inventory';

      case 'ADDITIONAL_INFORMATION':
        return 'supplemental information';

      case 'TOTAL_EMISSIONS':
        return 'total emissions for the year';

      case 'ACTIVITY_LEVEL_REPORT':
        return 'activity level report';

      default:
        break;
    }
  }
}
