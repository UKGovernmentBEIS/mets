import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitVariationStore } from '../../store/permit-variation.store';

@Component({
  selector: 'app-about-variation-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notificationBanner = this.router.getCurrentNavigation()?.extras.state?.notification;
  permitVariationDetails$ = this.store.pipe(map((state) => state.permitVariationDetails));
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  actionId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('actionId'))));
  requestTaskType$ = this.store.pipe(map((response) => response.requestTaskType));

  constructor(
    private readonly router: Router,
    readonly store: PermitVariationStore,
    private readonly route: ActivatedRoute,
  ) {}
}
