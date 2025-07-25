import { ChangeDetectionStrategy, Component } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';

import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { areCategoryTierPrerequisitesMet } from '../measurement-status';

@Component({
  selector: 'app-category-tier',
  templateUrl: './category-tier.component.html',
  styles: `
    app-page-heading button {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryTierComponent {
  readonly stateFeatures = toSignal(this.store.pipe(map((state) => state.features)));

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = combineLatest([this.index$, this.store]).pipe(
    map(
      ([index, state]) =>
        areCategoryTierPrerequisitesMet(state) ||
        !!(state.permit.monitoringApproaches?.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
          ?.emissionPointCategoryAppliedTiers?.[index],
    ),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly configStore: ConfigStore,
  ) {}
}
