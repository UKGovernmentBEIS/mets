import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { variationDetailsStatus } from '../../variation-status';

@Component({
  selector: 'app-about-variation-review',
  templateUrl: './about-variation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AboutVariationComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aboutVariationStatus$ = this.store.pipe(map((state) => variationDetailsStatus(state)));
  permitVariationDetails$ = this.store.pipe(map((state) => state.permitVariationDetails));

  isVariationRegulatorLed = this.store.isVariationRegulatorLedRequest;

  canViewSectionDetails$ = combineLatest([this.store.isEditable$, this.aboutVariationStatus$]).pipe(
    map(([isEditable, aboutVariationStatus]) => isEditable || aboutVariationStatus !== 'not started'),
  );

  constructor(private readonly router: Router, private readonly store: PermitVariationStore) {}
}
