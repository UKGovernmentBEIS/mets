import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { format } from '@shared/utils/bignumber.utils';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { getNonSustainableBiomassEmissions } from '../../../../../shared/components/review-groups/emissions-summary-group/emissions-summary-group';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
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

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  emissionPointEmissions$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions?.[index];
      return res;
    }),
  );

  nonSustainableBiomass$ = this.emissionPointEmissions$.pipe(
    map((emissionPointEmission) => {
      const result = emissionPointEmission?.biomassPercentages?.contains
        ? format(
            getNonSustainableBiomassEmissions(
              emissionPointEmission.reportableEmissions,
              emissionPointEmission?.sustainableBiomassEmissions,
              emissionPointEmission?.biomassPercentages?.nonSustainableBiomassPercentage,
            ),
          )
        : null;

      return result;
    }),
  );

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

  onDelete() {
    const nextStep = '../delete';
    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;
    const emissionPointEmission = measurement.emissionPointEmissions?.[index];

    const calculationCorrect = this.form.controls.calculationCorrect.value;

    const calculationReviewData = {
      calculationCorrect: calculationCorrect,
      measurementAdditionalInformation: {
        biomassEmissionsCorroboratingCalculation:
          this.form.controls.biomassEmissionsCorroboratingCalculation.value ?? null,
        biomassEmissionsTotalEnergyContent: this.form.controls.biomassEmissionsTotalEnergyContent.value ?? null,
        fossilEmissionsCorroboratingCalculation:
          this.form.controls.fossilEmissionsCorroboratingCalculation.value ?? null,
        fossilEmissionsTotalEnergyContent: this.form.controls.fossilEmissionsTotalEnergyContent.value ?? null,
      },
      ...(!calculationCorrect
        ? {
            providedEmissions: {
              reasonForProvidingManualEmissions: this.form.controls.reasonForProvidingManualEmissions.value,
              totalProvidedReportableEmissions: this.form.controls.totalProvidedReportableEmissions.value,
              ...(emissionPointEmission?.biomassPercentages?.contains
                ? {
                    totalProvidedSustainableBiomassEmissions:
                      this.form.controls.totalProvidedSustainableBiomassEmissions.value,
                  }
                : {}),
            },
          }
        : { providedEmissions: null }),
    };

    const sourceStreamEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...calculationReviewData,
          }
        : item,
    );

    const data = buildTaskData(this.taskKey, payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../summary';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
