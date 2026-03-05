import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions, PfcSourceStreamEmission } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_PFC_FORM, buildTaskData } from '../pfc';
import { primaryAluminiumFormProvider } from './primary-aluminium.provider';

@Component({
  selector: 'app-primary-aluminium',
  templateUrl: './primary-aluminium.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [primaryAluminiumFormProvider, DestroySubject],
})
export class PrimaryAluminiumComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<PfcSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  constructor(
    @Inject(AER_PFC_FORM) readonly form: UntypedFormGroup,
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
              getCompletionStatus('CALCULATION_PFC', payload, index, false),
              'CALCULATION_PFC',
            ),
          ),

          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...{ totalPrimaryAluminium: this.form.get('totalPrimaryAluminium').value },
            calculationCorrect: null,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    this.sourceStreamEmission$.pipe(takeUntil(this.destroy$)).subscribe((sourceStreamEmission) => {
      let nextStep = '';

      const calculationMethod = (sourceStreamEmission as PfcSourceStreamEmission)
        ?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod;

      if (calculationMethod === 'SLOPE') {
        nextStep = '../method-a';
      } else {
        nextStep = '../method-b';
      }

      this.router.navigate([`${nextStep}`], { relativeTo: this.route });
    });
  }
}
