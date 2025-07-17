import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { InstallationConnection } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-connection-delete',
  templateUrl: './connection-delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    .nowrap {
      white-space: nowrap;
    }
  `,
})
export class ConnectionDeleteComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  connection$ = combineLatest([this.store.getTask('monitoringMethodologyPlans'), this.route.paramMap]).pipe(
    map(([monitoringMethodologyPlans, paramMap]) =>
      monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.connections?.find(
        (connection) => connection.connectionNo === paramMap.get('connectionIndex'),
      ),
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) =>
          this.store.patchTask(
            permitTask,
            {
              ...state.permit.monitoringMethodologyPlans,
              digitizedPlan: {
                ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                installationDescription: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription,
                  connections: this.reIndexConnectionsAndRemove(
                    state.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.connections,
                    this.route.snapshot.paramMap.get('connectionIndex'),
                  ),
                },
              },
            },
            false,
            'mmpInstallationDescription',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  reIndexConnectionsAndRemove(connections: InstallationConnection[], index: string) {
    connections.splice(+index, 1);

    return connections.map((conn, index) => {
      return { ...conn, connectionNo: index + '' };
    });
  }
}
