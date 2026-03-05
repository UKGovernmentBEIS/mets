import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import {
  connectionsAddFormFactory,
  connectionTypeOptions,
  entityTypeOptions,
  flowDirectionOptions,
} from './connection-details-form.provider';

@Component({
  selector: 'app-connection-details',
  templateUrl: './connection-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [connectionsAddFormFactory],
})
export class ConnectionDetailsComponent implements PendingRequest {
  requestTaskType$ = this.store.pipe(map((response) => reviewRequestTaskTypes.includes(response.requestTaskType)));
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('connectionIndex') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  entityTypeOptions = entityTypeOptions;
  connectionTypeOptions = connectionTypeOptions;
  flowDirectionOptions = flowDirectionOptions;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  onSubmit(): void {
    combineLatest([this.permitTask$, this.store, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([permitTask, state, paramMap]) => {
          const installationDescription =
            state.permit?.monitoringMethodologyPlans?.digitizedPlan?.installationDescription;

          const connections = installationDescription && installationDescription?.connections;

          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  installationDescription: {
                    ...installationDescription,

                    connections: connections?.some((item) => item.connectionNo === paramMap.get('connectionIndex'))
                      ? connections?.map((item, index) =>
                          item.connectionNo === paramMap.get('connectionIndex')
                            ? { ...this.form.value, connectionNo: paramMap.get('connectionIndex') }
                            : { ...item, connectionNo: '' + index },
                        )
                      : connections && connections?.length
                        ? [
                            ...connections.map((item, index) => {
                              return { ...item, connectionNo: '' + index };
                            }),
                            {
                              ...this.form.value,
                              connectionNo: (connections?.length ?? 0) + '',
                            },
                          ]
                        : [
                            {
                              ...this.form.value,
                              connectionNo: (connections?.length ?? 0) + '',
                            },
                          ],
                  },
                },
              },
              false,
              'mmpInstallationDescription',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
      )
      .subscribe(() => {
        this.router.navigate(['../'], { relativeTo: this.route });
      });
  }
}
