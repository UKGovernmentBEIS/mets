import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, iif, map, of, shareReplay, switchMap } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationNationalInventoryDataCalculationMethod,
  CalculationOfCO2Emissions,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { buildParameterCalculationMethodData, buildTaskData } from '../calculation-emissions';
import { NationalInventoryService } from '../services/national-inventory.service';

@Component({
  selector: 'app-relevant-category',
  templateUrl: './relevant-category.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelevantCategoryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  payload$ = this.aerService.getPayload();

  nationalInventoryData$ = this.nationalInventoryService.nationalInventoryData$;

  parameterCalculationMethod$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      return (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index]
        ?.parameterCalculationMethod as CalculationNationalInventoryDataCalculationMethod;
    }),
  );

  form$ = combineLatest([this.parameterCalculationMethod$, this.nationalInventoryData$, this.isEditable$]).pipe(
    map(([parameterCalculationMethod, nationalInventoryData, isEditable]) => {
      const mainActivitySector = parameterCalculationMethod?.mainActivitySector;

      const controls = {};
      nationalInventoryData.sectors.forEach((sector) => {
        controls[`sector_${sector.name}`] = [
          {
            value:
              mainActivitySector === sector.name
                ? parameterCalculationMethod?.fuel
                  ? parameterCalculationMethod.fuel
                  : null
                : null,
            disabled: !isEditable,
          },
        ];
      });

      return this.fb.group(
        {
          sector: [
            {
              value: mainActivitySector,
              disabled: !isEditable,
            },
          ],
          ...controls,
        },
        {
          validators: [this.validateForm()],
        },
      );
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    readonly aerService: AerService,
    readonly nationalInventoryService: NationalInventoryService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  onContinue(): void {
    combineLatest([this.form$, this.payload$, this.index$])
      .pipe(
        first(),
        filter(([form]) => form.valid),
        switchMap(([form, payload, index]) =>
          iif(
            () => form.dirty,
            this.aerService.postTaskSave(
              this.buildData(form, payload, index),
              undefined,
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
            of(null),
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(form, payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sector = form.get('sector').value;
    const fuel = form.get(`sector_${sector}`).value;

    const parameterCalculationMethodData = {
      mainActivitySector: sector,
      fuel: fuel,
    };

    const parameterCalculationMethod = buildParameterCalculationMethodData(
      calculation.sourceStreamEmissions?.[index],
      parameterCalculationMethodData,
    );

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...parameterCalculationMethod,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    const nextStep = '../activity-calculation-method';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }

  private validateForm() {
    return (group: UntypedFormGroup): ValidationErrors => {
      const sectorValue = group.get('sector').value;

      if (!sectorValue) {
        group.controls['sector'].setErrors({ sectorInvalid: 'Select an option' });
      } else {
        const fuelValue = group.get(`sector_${sectorValue}`)?.value;

        if (!fuelValue) {
          const sectorControl = group.controls[`sector_${sectorValue}`];
          sectorControl && sectorControl.setErrors({ fuelInvalid: 'Select an option' });
        }
      }
      return null;
    };
  }
}
