import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { DoalService } from '../../../core/doal.service';

@Component({
  selector: 'app-activity-levels',
  templateUrl: './activity-levels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelsComponent {
  private readonly nextWizardStep = 'estimates';

  historicalActivityLevels$ = this.doalService.payload$.pipe(map((payload) => payload.historicalActivityLevels));
  activityLevels$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal.activityLevelChangeInformation.activityLevels),
  );
  isEditable$ = this.doalService.isEditable$;

  constructor(
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route });
  }
}
