import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ManagementProceduresDefinitionData } from '../management-procedures.interface';

@Component({
  selector: 'app-management-procedures-summary',
  templateUrl: './management-procedures-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  permitTask$ = this.route.data.pipe(
    map<ManagementProceduresDefinitionData, ManagementProceduresDefinitionData['permitTask']>(
      (data) => data?.permitTask,
    ),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
