import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { buildTaskData } from '@tasks/aer/submit/inherent-co2/inherent-co2';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-total-emissions',
  templateUrl: './emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider],
})
export class EmissionsComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  inherentInstallation$ = combineLatest([this.aerService.aerInherentInstallations$, this.index$]).pipe(
    map(([installations, index]) => installations?.[index]?.inherentReceivingTransferringInstallation),
  );
  heading$ = this.inherentInstallation$.pipe(
    map((inherentInstallation) =>
      inherentInstallation?.inherentCO2Direction === 'RECEIVED_FROM_ANOTHER_INSTALLATION'
        ? 'Calculate the total emissions received from the installation'
        : 'Calculate the total emissions exported to the installation',
    ),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../..';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      combineLatest([
        this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>,
        this.inherentInstallation$,
        this.index$,
      ])
        .pipe(
          first(),
          switchMap(([payload, installation, index]) =>
            this.aerService.postTaskSave(
              buildTaskData(
                payload,
                {
                  ...installation,
                  totalEmissions: this.form.controls.totalEmissions.value,
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
