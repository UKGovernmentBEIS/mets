import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { DoalService } from '../../../core/doal.service';

@Component({
  selector: 'app-preliminary-allocations',
  templateUrl: './preliminary-allocations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreliminaryAllocationsComponent {
  private readonly nextWizardStep = 'comments';

  preliminaryAllocations$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal.activityLevelChangeInformation.preliminaryAllocations),
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
