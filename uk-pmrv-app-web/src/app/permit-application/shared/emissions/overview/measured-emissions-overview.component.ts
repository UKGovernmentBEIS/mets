import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, switchMap } from 'rxjs';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfCO2MeasuredEmissions,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  MeasurementOfN2OMeasuredEmissions,
} from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-measured-emissions-overview',
  templateUrl: './measured-emissions-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasuredEmissionsOverviewComponent {
  @Input() measuredEmissions: MeasurementOfCO2MeasuredEmissions & MeasurementOfN2OMeasuredEmissions;
  @Input() cssClass: string;

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  taskKey$ = this.route.data.pipe(map((x) => x?.taskKey.split('.')[1]));

  task$ = this.route.data.pipe(
    switchMap((data) =>
      this.store.findTask<
        MeasurementOfN2OEmissionPointCategoryAppliedTier[] | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
      >(data.taskKey),
    ),
  );

  files$ = combineLatest([this.index$, this.task$]).pipe(
    map(([index, tiers]) => [...(tiers?.[index].measuredEmissions?.noHighestRequiredTierJustification?.files ?? [])]),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
  ) {}
}
