import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions, ReportingService } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_PFC_FORM, buildTaskData } from '../pfc';
import { methodsFormProvider } from './methods.provider';

@Component({
  selector: 'app-methods',
  templateUrl: './methods.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [methodsFormProvider],
})
export class MethodsComponent {
  methodType = this.route.snapshot.data.methodType;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_PFC_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly reportingService: ReportingService,
  ) {}

  onContinue(): void {
    combineLatest([this.payload$, this.index$])
      .pipe(
        first(),
        switchMap(([payload, index]) => {
          const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
          const pfc = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

          const sourceStreamEmission = pfc.sourceStreamEmissions?.[index];

          const parameters = {
            calculationMethod: sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod,
            totalPrimaryAluminium: sourceStreamEmission.totalPrimaryAluminium,
            ...this.getFormData(sourceStreamEmission),
          };

          return this.reportingService.calculatePfcEmissions(parameters as any);
        }),
        withLatestFrom(this.payload$, this.index$),
        switchMap(([totals, payload, index]) =>
          this.aerService.postTaskSave(
            this.buildData(totals, payload, index),
            undefined,
            getCompletionStatus('CALCULATION_PFC', payload, index, false),
            'CALCULATION_PFC',
          ),
        ),

        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(totals, payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const pfc = monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions;

    const sourceStreamEmission = pfc.sourceStreamEmissions[index];

    const formData = this.getFormData(sourceStreamEmission);

    const sourceStreamEmissions = pfc.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...formData,
            amountOfCF4: totals.amountOfCF4,
            totalCF4Emissions: totals.totalCF4Emissions,
            amountOfC2F6: totals.amountOfC2F6,
            totalC2F6Emissions: totals.totalC2F6Emissions,
            reportableEmissions: totals.reportableEmissions,
            calculationCorrect: null,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../calculation-review';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private getFormData(sourceStreamEmission) {
    return {
      pfcSourceStreamEmissionCalculationMethodData: {
        calculationMethod: sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod,
        ...(this.methodType === 'METHOD_A'
          ? {
              anodeEffectsPerCellDay: this.form.get('anodeEffectsPerCellDay').value,
              averageDurationOfAnodeEffectsInMinutes: this.form.get('averageDurationOfAnodeEffectsInMinutes').value,
              slopeCF4EmissionFactor: this.form.get('slopeCF4EmissionFactor').value,
            }
          : {}),
        ...(this.methodType === 'METHOD_B'
          ? {
              anodeEffectsOverVoltagePerCell: this.form.get('anodeEffectsOverVoltagePerCell').value,
              aluminiumAverageCurrentEfficiencyProduction: this.form.get('aluminiumAverageCurrentEfficiencyProduction')
                .value,
              overVoltageCoefficient: this.form.get('overVoltageCoefficient').value,
            }
          : {}),
        c2F6WeightFraction: this.form.get('c2F6WeightFraction').value,
        percentageOfCollectionEfficiency: this.form.get('percentageOfCollectionEfficiency').value,
      },
    };
  }
}
