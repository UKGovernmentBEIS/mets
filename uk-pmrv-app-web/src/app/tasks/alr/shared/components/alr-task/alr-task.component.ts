import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { ALRReturnLinkComponent } from '../alr-return-link/alr-return-link.component';

@Component({
  selector: 'app-alr-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
    <app-alr-return-link [returnLink]="returnLink" [title]="returnLinkTitle"></app-alr-return-link>
  `,
  standalone: true,
  imports: [RouterModule, SharedModule, ALRReturnLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AlrTaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;
  @Input() returnLink?;
  @Input() returnLinkTitle: string = 'Activity level report';
}
