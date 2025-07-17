import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { AnnualEmissionTarget } from '../determination.type';

export abstract class SummaryDetailsAbstractComponent {
  determination$: Observable<any> = this.store.pipe(map((state) => state.determination));
  determinationText$ = this.store.getDeterminationType$();

  emissionsTargets$: Observable<AnnualEmissionTarget[]> = this.store.pipe(
    map(
      (state) =>
        state.determination.annualEmissionsTargets &&
        Object.keys(state.determination.annualEmissionsTargets).map(
          (key) =>
            ({
              year: key,
              target: state.determination.annualEmissionsTargets[key],
            }) as AnnualEmissionTarget,
        ),
    ),
  );

  isPermitTypeHSE$: Observable<boolean> = this.store.pipe(map((state) => state.permitType === 'HSE'));

  constructor(
    protected readonly store: PermitApplicationStore<PermitApplicationState>,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
