import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { InherentReceivingTransferringInstallation } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { directionFormProvider } from './direction-form.provider';

@Component({
  selector: 'app-direction',
  templateUrl: './direction.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, directionFormProvider],
})
export class DirectionComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    combineLatest([
      this.index$,
      this.store.findTask<InherentReceivingTransferringInstallation[]>(
        'monitoringApproaches.INHERENT_CO2.inherentReceivingTransferringInstallations',
      ),
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, installations, data]) => {
          const inherentReceivingTransferringInstallations =
            installations && installations[index]
              ? installations.map((installation, idx) =>
                  index === idx
                    ? {
                        ...installation,
                        inherentCO2Direction: this.form.controls.inherentCO2Direction.value,
                      }
                    : installation,
                )
              : [
                  ...(installations ?? []),
                  {
                    inherentCO2Direction: this.form.controls.inherentCO2Direction.value,
                  },
                ];

          return this.store
            .patchTask(data.taskKey, { inherentReceivingTransferringInstallations }, false, data.statusKey)
            .pipe(this.pendingRequest.trackRequest());
        }),
      )
      .subscribe(() => this.router.navigate(['../details'], { relativeTo: this.route }));
  }
}
