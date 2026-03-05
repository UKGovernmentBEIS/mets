import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
import { operationHoursFormProvider } from './operation-hours-form.provider';

@Component({
  selector: 'app-operation-hours',
  templateUrl: './operation-hours.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [operationHoursFormProvider],
})
export class OperationHoursComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_MEASUREMENT_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.payload$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, index),
              undefined,
              getCompletionStatus(this.taskKey, payload, index, false),
              this.taskKey,
            ),
          ),

          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            operationalHours: this.form.get('operationalHours').value,
            calculationCorrect: null,
          }
        : item,
    );

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }
  private navigateNext() {
    const nextStep = 'gas-flow';

    this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
  }
}
