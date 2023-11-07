import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { statusKeyToSubtaskNameMapper } from '../category-tier';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  readonly statusKey = this.route.snapshot.data.statusKey;
  readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(private readonly route: ActivatedRoute, private readonly router: Router) {}
}
