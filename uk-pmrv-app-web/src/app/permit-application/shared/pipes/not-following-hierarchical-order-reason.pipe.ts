import { Pipe, PipeTransform } from '@angular/core';

import { SubInstallationHierarchicalOrder } from 'pmrv-api';

@Pipe({
  name: 'notFollowingHierarchicalOrderReason',
})
export class NotFollowingHierarchicalOrderReasonPipe implements PipeTransform {
  transform(value: SubInstallationHierarchicalOrder['notFollowingHierarchicalOrderReason']): string {
    switch (value) {
      case 'OTHER_DATA_SOURCES_LEAD_TO_LOWER_UNCERTAINTY':
        return 'Other data sources lead to lower uncertainty according to the simplified uncertainty assessment in line with Article 7(2) of the FAR';
      case 'USE_OF_BETTER_DATA_SOURCES_TECHNICALLY_INFEASIBLE':
        return 'Use of better data sources is technically infeasible';
      case 'USE_OF_BETTER_DATA_SOURCES_UNREASONABLE_COSTS':
        return 'Use of better data sources would incur unreasonable costs';
      default:
        return '';
    }
  }
}
