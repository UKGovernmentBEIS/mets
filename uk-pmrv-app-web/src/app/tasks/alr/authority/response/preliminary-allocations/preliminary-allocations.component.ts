import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { AllocationListTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-alr-preliminary-allocations',
  templateUrl: './preliminary-allocations.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, AllocationListTemplateComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ALRPreliminaryAllocationsComponent {
  preliminaryAllocations$ = this.alrService.authorityPayload$.pipe(
    map(
      (payload) =>
        (payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse).preliminaryAllocations,
    ),
  );
  isEditable$ = this.alrService.isEditable$;

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../', 'approved-allocations'], { relativeTo: this.route });
  }
}
