import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  MeasurementOfN2OMeasuredEmissions,
} from 'pmrv-api';

import { areMeasMeasuredEmissionsPrerequisitesMet } from '../../approaches/measurement/measurement-status';
import { areN2OMeasuredEmissionsPrerequisitesMet } from '../../approaches/n2o/n2o-status';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { StatusKey, TaskKey } from '../types/permit-task.type';
import { EMISSIONS_FORM, emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-emissions',
  templateUrl: './emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider, DestroySubject],
})
export class EmissionsComponent implements OnInit {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  taskKey$ = this.route.data.pipe(map((x) => x?.taskKey));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<
        MeasurementOfN2OEmissionPointCategoryAppliedTier[] | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
      >(`monitoringApproaches.${data.taskKey}.emissionPointCategoryAppliedTiers`),
    ),
  );

  measuredEmissionsNotApplicable$ = combineLatest([this.index$, this.taskKey$, this.store]).pipe(
    map(([index, taskKey, state]) =>
      taskKey === 'MEASUREMENT_CO2'
        ? !areMeasMeasuredEmissionsPrerequisitesMet(state, index)
        : !areN2OMeasuredEmissionsPrerequisitesMet(state, index),
    ),
  );

  typeOptions: { value: MeasurementOfN2OMeasuredEmissions['samplingFrequency']; label: string }[] = [
    { value: 'CONTINUOUS', label: 'Continuous' },
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'BI_ANNUALLY', label: 'Bi annually' },
    { value: 'ANNUALLY', label: 'Annually' },
  ];

  constructor(
    @Inject(EMISSIONS_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.form
      .get('tier')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value === 'TIER_3') {
          this.form.patchValue({ isHighestRequiredTier: null });
        }
      });
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['justification'], { relativeTo: this.route });
    } else {
      combineLatest([this.index$, this.task$, this.taskKey$, this.store])
        .pipe(
          first(),
          switchMap(([index, tiers, taskKey, state]) =>
            this.store.postTask(
              `monitoringApproaches.${taskKey}.emissionPointCategoryAppliedTiers` as TaskKey,
              tiers.map((item, idx) =>
                idx === index
                  ? {
                      ...item,
                      measuredEmissions: {
                        ...(this.form.value.isHighestRequiredTierT1 === false ||
                        this.form.value.isHighestRequiredTierT2 === false ||
                        this.form.value.isHighestRequiredTierT3 === false
                          ? item.measuredEmissions
                          : null),
                        measurementDevicesOrMethods: this.form.value.measurementDevicesOrMethods,
                        samplingFrequency: this.form.value.samplingFrequency,
                        tier: this.form.value.tier,
                        otherSamplingFrequency: this.form.value.otherSamplingFrequency,
                        ...{
                          isHighestRequiredTier:
                            this.form.value.isHighestRequiredTierT1 ??
                            this.form.value.isHighestRequiredTierT2 ??
                            this.form.value.isHighestRequiredTierT3,
                        },
                      },
                    }
                  : item,
              ),
              state.permitSectionsCompleted[`${taskKey}_Measured_Emissions`].map((item, idx) =>
                index === idx ? false : item,
              ),
              `${taskKey}_Measured_Emissions` as StatusKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['justification'], { relativeTo: this.route }));
    }
  }
}
