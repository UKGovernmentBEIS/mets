import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, switchMap, tap } from 'rxjs';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
} from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { PERMIT_TASK_FORM } from '../../permit-task-form.token';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrl: './summary.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider],
})
export class SummaryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  taskKey$ = this.route.data.pipe(map((x) => x?.taskKey));
  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<
        MeasurementOfN2OEmissionPointCategoryAppliedTier[] | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
      >(`monitoringApproaches.${data.taskKey}.emissionPointCategoryAppliedTiers`),
    ),
  );

  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].measuredEmissions?.noHighestRequiredTierJustification?.files ?? [])]),
  );

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.validMeasurementDevicesOrMethods),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
