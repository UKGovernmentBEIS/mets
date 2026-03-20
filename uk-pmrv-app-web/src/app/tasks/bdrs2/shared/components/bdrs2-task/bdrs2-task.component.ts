import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { BDRS2ReturnLinkComponent } from '../return-link/return-link.component';

@Component({
  selector: 'app-bdrs2-task',
  imports: [RouterModule, SharedModule, BDRS2ReturnLinkComponent],
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
    <app-bdrs2-return-link [returnLink]="returnLink" [title]="returnLinkTitle"></app-bdrs2-return-link>
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2TaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;
  @Input() returnLink?;
  @Input() returnLinkTitle: string = 'Stage 2 baseline data report';
}
