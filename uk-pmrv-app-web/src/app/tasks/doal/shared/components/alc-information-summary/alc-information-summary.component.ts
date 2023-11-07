import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

import { ActivityLevelChangeInformation } from 'pmrv-api';

@Component({
  selector: 'app-alc-information-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Provide information about this activity level change</app-page-heading>
      <app-doal-alc-information-template
        *ngIf="activityLevelChangeInformation$ | async as alc"
        [data]="alc"
      ></app-doal-alc-information-template>
      <app-task-return-link [levelsUp]="2" [taskType]="taskType$ | async"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlcInformationSummaryComponent {
  taskType$ = this.doalService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  activityLevelChangeInformation$: Observable<ActivityLevelChangeInformation> = this.doalService.payload$.pipe(
    map((payload) => payload.doal.activityLevelChangeInformation),
  );

  constructor(private readonly doalService: DoalService) {}
}
