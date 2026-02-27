import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { DoalService } from '../../../core/doal.service';

@Component({
  selector: 'app-doal-activity-levels',
  standalone: false,
  templateUrl: './activity-levels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalActivityLevelsComponent {
  private readonly nextWizardStep = 'estimates';

  historicalActivityLevels$ = this.doalService.payload$.pipe(map((payload) => payload.historicalActivityLevels));
  isEditable$ = this.doalService.isEditable$;
  activityLevels$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal.activityLevelChangeInformation.activityLevels),
  );

  constructor(
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../', this.nextWizardStep], { relativeTo: this.route });
  }
}
