import { ChangeDetectionStrategy, Component, computed, Input, Signal } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';

import { HSETIReturnLinkComponent } from '../hseti-return-link/hseti-return-link.component';

@Component({
  selector: 'app-hseti-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <ng-content></ng-content>
      </div>
    </div>
    <app-hseti-return-link [returnLink]="returnLink" [title]="returnLinkTitle()"></app-hseti-return-link>
  `,
  standalone: true,
  imports: [RouterModule, SharedModule, HSETIReturnLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class HseTiTaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
  @Input() reviewGroupTitle: any;
  @Input() reviewGroupUrl: any;
  @Input() returnLink?;

  allocationPeriod: Signal<string> = this.hseTiService.allocationPeriod;

  returnLinkTitle: Signal<string> = computed(
    () => `Complete ${this.allocationPeriod()} HSE target increase application`,
  );

  constructor(private readonly hseTiService: HseTiService) {}
}
