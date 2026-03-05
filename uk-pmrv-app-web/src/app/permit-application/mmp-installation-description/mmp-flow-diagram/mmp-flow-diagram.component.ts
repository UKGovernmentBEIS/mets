import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { MmpFlowDiagramProvider } from './mmp-flow-diagram-form.provider';

@Component({
  selector: 'app-mmp-flow-diagram',
  templateUrl: './mmp-flow-diagram.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [MmpFlowDiagramProvider],
})
export class MmpFlowDiagramComponent implements PendingRequest {
  readonly isFileUploaded$: Observable<boolean> = this.form.get('flowDiagrams').valueChanges.pipe(
    startWith(this.form.get('flowDiagrams').value),
    map((value) => value?.length > 0),
  );
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) =>
          this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  installationDescription: {
                    ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription,
                    flowDiagrams: this.form.value.flowDiagrams?.map((file) => file.uuid),
                  },
                },
              },
              false,
              'mmpInstallationDescription',
            )
            .pipe(this.pendingRequest.trackRequest()),
        ),
      )
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.flowDiagrams?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });
        this.router.navigate(['../connections'], { relativeTo: this.route });
      });
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }
}
