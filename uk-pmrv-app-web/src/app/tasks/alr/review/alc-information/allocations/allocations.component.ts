import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { AllocationListTemplateComponent } from '@shared/components/alr';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

@Component({
  selector: 'app-alr-allocations',
  imports: [SharedModule, AlrTaskSharedModule, RouterLink, AllocationListTemplateComponent],
  templateUrl: './allocations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAllocationsComponent {
  allocations$ = this.alrService.payload$.pipe(map((payload) => payload?.regulatorReviewOutcome?.allocations));
  isEditable$ = this.alrService.isEditable$;

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../comments'], { relativeTo: this.route });
  }
}
