import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { iif, map } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-approaches-summary-template',
  template: `
    <dl govuk-summary-list [class.dl--no-bottom-border]="!hasBottomBorder">
      <div govukSummaryListRow *ngFor="let monitoringApproach of monitoringApproaches$ | async">
        <dt govukSummaryListRowKey>{{ monitoringApproach | monitoringApproachDescription }}</dt>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesSummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;

  monitoringApproaches$ = iif(
    () => this.showOriginal,
    this.store.getOriginalTask('monitoringApproaches'),
    this.store.getTask('monitoringApproaches'),
  ).pipe(
    map((monitoringApproaches) =>
      monitoringApproachTypeOptions.filter((option) => Object.keys(monitoringApproaches ?? {}).includes(option)),
    ),
  );

  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
