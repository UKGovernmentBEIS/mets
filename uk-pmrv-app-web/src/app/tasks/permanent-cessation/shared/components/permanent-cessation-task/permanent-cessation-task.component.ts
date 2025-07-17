import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-permanent-cessation-task',
  template: `
    <div class="govuk-!-width-three-quarters">
      <govuk-notification-banner *ngIf="notification" type="success">
        <h1 class="govuk-notification-banner__heading">Details updated</h1>
      </govuk-notification-banner>
      <ng-content></ng-content>
      <a govukLink [routerLink]="returnToLink">Return to: Permanent cessation</a>
    </div>
  `,
  standalone: true,
  imports: [RouterModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class PermanentCessationTaskComponent {
  @Input() notification: any;
  @Input() returnToLink = '..';
}
