import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions, PfcSourceStreamEmission } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_PFC_FORM, buildTaskData } from '../pfc';
import { calculationReviewFormProvider } from './calculation-review.provider';

@Component({
  selector: 'app-calculation-review',
  templateUrl: './calculation-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [calculationReviewFormProvider],
})
export class CalculationReviewComponent {
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
    const pfc = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

    const calculationCorrect = this.form.controls.calculationCorrect.value;

    const pfcReviewData = {
      calculationCorrect: calculationCorrect,
      ...(!calculationCorrect
        ? {
            providedEmissions: {
              reasonForProvidingManualEmissions: this.form.controls.reasonForProvidingManualEmissions.value,
              totalProvidedReportableEmissions: this.form.controls.totalProvidedReportableEmissions.value,
            },
          }
        : { providedEmissions: null }),
    };

    const sourceStreamEmissions = pfc.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...pfcReviewData,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../summary';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
