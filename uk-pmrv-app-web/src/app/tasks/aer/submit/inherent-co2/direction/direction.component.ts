import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { buildTaskData } from '@tasks/aer/submit/inherent-co2/inherent-co2';

import { AerApplicationSubmitRequestTaskPayload, InherentReceivingTransferringInstallation } from 'pmrv-api';

import { directionFormProvider } from './direction-form.provider';

@Component({
  selector: 'app-direction',
  templateUrl: './direction.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [directionFormProvider],
})
export class DirectionComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  radioOptions: InherentReceivingTransferringInstallation['inherentCO2Direction'][] = [
    'EXPORTED_TO_ETS_INSTALLATION',
    'EXPORTED_TO_NON_ETS_CONSUMER',
    'RECEIVED_FROM_ANOTHER_INSTALLATION',
  ];

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../details';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      combineLatest([
        this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>,
        this.aerService.aerInherentInstallations$,
        this.index$,
      ])
        .pipe(
          first(),
          switchMap(([payload, aerInherentInstallations, index]) =>
            this.aerService.postTaskSave(
              buildTaskData(
                payload,
                {
                  ...aerInherentInstallations?.[index]?.inherentReceivingTransferringInstallation,
                  inherentCO2Direction: this.form.controls.inherentCO2Direction.value,
                },
                index,
              ),
              undefined,
              false,
              'INHERENT_CO2',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
