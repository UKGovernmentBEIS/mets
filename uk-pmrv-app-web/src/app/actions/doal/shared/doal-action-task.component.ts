import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { RequestActionDTO } from 'pmrv-api';

@Component({
  selector: 'app-doal-action-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>{{ header }}</app-page-heading>
        <ng-content></ng-content>
        <a govukLink [routerLink]="['..']">Return to: {{ actionType | itemActionType }}</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalActionTaskComponent {
  @Input() header: string;
  @Input() actionType: RequestActionDTO['type'];
}
