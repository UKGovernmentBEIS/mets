import { Pipe, PipeTransform } from '@angular/core';

import { RegulatedActivity } from 'pmrv-api';

@Pipe({
  name: 'regulatedActivity',
  standalone: false,
})
export class RegulatedActivityPipe implements PipeTransform {
  transform(regulatedActivities: RegulatedActivity[], regulatedActivityId: string): RegulatedActivity {
    return regulatedActivities.find((activity) => activity.id === regulatedActivityId);
  }
}
