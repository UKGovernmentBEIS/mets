import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_CALCULATION_EMISSIONS_FORM, buildTaskData } from '../calculation-emissions';
import { calculationMethodFormProvider } from './calculation-method.provider';

@Component({
  selector: 'app-calculation-method',
  templateUrl: './calculation-method.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [calculationMethodFormProvider, DestroySubject],
})
export class CalculationMethodComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  parameterCalculationMethod$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index]?.parameterCalculationMethod;
    }),
  );

  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_CALCULATION_EMISSIONS_FORM) readonly form: UntypedFormGroup,
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
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];
    const emissionCalculationParamValues = (sourceStreamEmission?.parameterCalculationMethod as any)
      ?.emissionCalculationParamValues;

    const currentCalculationMethod = sourceStreamEmission?.parameterCalculationMethod?.type;

    const formPayload = {
      parameterCalculationMethod: {
        type: this.form.get('type').value,
        calculationActivityDataCalculationMethod:
          calculation.sourceStreamEmissions?.[index]?.parameterCalculationMethod
            ?.calculationActivityDataCalculationMethod,
        ...(currentCalculationMethod === 'MANUAL' &&
        this.form.get('type').value === 'MANUAL' &&
        emissionCalculationParamValues
          ? { emissionCalculationParamValues: emissionCalculationParamValues }
          : {}),
      },
    };

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...formPayload,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    this.parameterCalculationMethod$.pipe(takeUntil(this.destroy$)).subscribe((parameterCalculationMethod) => {
      let nextStep = '';

      switch (parameterCalculationMethod?.type) {
        case 'REGIONAL_DATA':
          nextStep = '../installation-postcode';
          break;
        case 'NATIONAL_INVENTORY_DATA':
          nextStep = '../relevant-category';
          break;
        case 'MANUAL':
          nextStep = '../activity-calculation-method';
          break;
        default:
          break;
      }

      this.router.navigate([`${nextStep}`], { relativeTo: this.route });
    });
  }
}
