import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-determination-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Provide determination of activity level</app-page-heading>
      <app-determination-summary-template [determination]="determination$ | async"></app-determination-summary-template>
      <app-task-return-link [levelsUp]="3" [taskType]="taskType$ | async"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationSummaryComponent {
  taskType$ = this.doalService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  determination$ = this.doalService.payload$.pipe(map((payload) => payload.doal?.determination as any));

  constructor(private readonly doalService: DoalService) {}
}
