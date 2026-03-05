import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { GovukTableColumn } from 'govuk-components';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { emissionSummariesColumns } from '../emission-summaries';

@Component({
  selector: 'app-emission-summaries-summary',
  templateUrl: './emission-summaries-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSummariesSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  emissionSummariesColumns: GovukTableColumn[] = emissionSummariesColumns;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
