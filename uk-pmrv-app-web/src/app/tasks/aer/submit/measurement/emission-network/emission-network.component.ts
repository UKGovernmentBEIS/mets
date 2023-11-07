import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
import { emissionNetworkFormProvider } from './emission-network.provider';

@Component({
  selector: 'app-emission-network',
  templateUrl: './emission-network.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionNetworkFormProvider, DestroySubject],
})
export class EmissionNetworkComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  emissionPointEmissions$ = combineLatest([this.payload$, this.index$]).pipe(
    filter(([payload]) => !!payload),
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions?.[index];
      return res;
    }),
  );

  private hasTransfer$: Observable<boolean> = this.payload$.pipe(
    map((payload) => (payload.aer.monitoringApproachEmissions[this.taskKey] as any)?.hasTransfer),
  );

  constructor(
    @Inject(AER_MEASUREMENT_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
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

  onDelete() {
    const nextStep = '../delete';
    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmission = measurement?.emissionPointEmissions?.[index];
    const containsBiomass = this.form.get('containsBiomass').value;

    const formData = {
      emissionPoint: this.form.get('emissionPoint').value,
      sourceStreams: this.form.get('sourceStreams').value,
      emissionSources: this.form.get('emissionSources').value,
      biomassPercentages: {
        ...(containsBiomass ? emissionPointEmission?.biomassPercentages : {}),
        contains: containsBiomass,
      },
    };

    const emissionPointEmissionPayload = {
      ...emissionPointEmission,
      ...formData,
      calculationCorrect: null,
    };

    const emissionPointEmissions =
      measurement?.emissionPointEmissions && measurement?.emissionPointEmissions?.[index]
        ? measurement.emissionPointEmissions.map((item, idx) => {
            return idx === index
              ? {
                  ...emissionPointEmissionPayload,
                }
              : item;
          })
        : [...(measurement?.emissionPointEmissions ?? []), emissionPointEmissionPayload];

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }
  private navigateNext() {
    this.hasTransfer$.pipe(takeUntil(this.destroy$)).subscribe((hasTransfer) => {
      let nextStep = '';

      if (hasTransfer === true) {
        nextStep = 'transferred';
      } else {
        nextStep = 'date-range';
      }
      this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
    });
  }
}
