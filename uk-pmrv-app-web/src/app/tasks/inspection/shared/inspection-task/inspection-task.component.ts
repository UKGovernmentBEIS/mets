import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';

@Component({
  selector: 'app-inspection-task',
  template: `
    @if (heading) {
      <app-page-heading [caption]="caption">{{ heading }}</app-page-heading>
    }
    <ng-content></ng-content>
    <a govukLink [routerLink]="returnToLink">Return to: {{ returnLinkText | async }}</a>
  `,
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InspectionTaskComponent {
  @Input() heading: string;
  @Input() caption: string;
  @Input() returnToLink = '..';

  returnLinkText: Observable<string> = this.inspectionService.requestTaskItem$.pipe(
    map((requestTask) => this.taskTypeToBreadcrumbPipe.transform(requestTask?.requestTask?.type)),
  );

  constructor(
    private readonly backLinkService: BackLinkService,
    private readonly inspectionService: InspectionService,
    private readonly taskTypeToBreadcrumbPipe: TaskTypeToBreadcrumbPipe,
  ) {
    this.backLinkService.show();
  }
}
