import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-bdr-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
  `,
  standalone: true,
  imports: [RouterModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class BdrTaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;
}
