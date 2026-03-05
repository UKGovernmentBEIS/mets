import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { AirService } from '@tasks/air/shared/services/air.service';

@Component({
  selector: 'app-air-task',
  template: `
    <app-page-heading>{{ heading }}</app-page-heading>
    <ng-content></ng-content>
    <a govukLink [routerLink]="returnToLink">Return to: {{ returnLinkText | async }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AirTaskComponent {
  @Input() heading: string;
  @Input() returnToLink = '..';

  returnLinkText: Observable<string> = this.airService.requestTaskItem$.pipe(
    map((requestTask) => this.taskTypeToBreadcrumbPipe.transform(requestTask?.requestTask?.type)),
  );

  constructor(
    private readonly backLinkService: BackLinkService,
    private readonly airService: AirService,
    private readonly taskTypeToBreadcrumbPipe: TaskTypeToBreadcrumbPipe,
  ) {
    this.backLinkService.show();
  }
}
