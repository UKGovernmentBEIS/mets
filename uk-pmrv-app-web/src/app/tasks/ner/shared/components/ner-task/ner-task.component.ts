import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { RequestTaskDTO } from 'pmrv-api';

@Component({
  selector: 'app-ner-task',
  imports: [SharedModule, TaskSharedModule],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-{{ contentWidth }}">
        <app-page-heading *ngIf="heading" [caption]="caption">{{ heading }}</app-page-heading>
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>

        <ng-content></ng-content>
      </div>
    </div>

    <app-task-return-link [taskType]="taskType" [levelsUp]="returnLinkLevelsUp"></app-task-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerTaskComponent {
  @Input() notification: any;
  @Input() heading: string;
  @Input() caption: string;
  @Input() taskType: RequestTaskDTO['type'];
  @Input() returnLinkLevelsUp = 1;
  @Input() contentWidth: 'one-quarter' | 'one-third' | 'one-half' | 'two-thirds' | 'three-quarters' | 'full' = 'full';
}
