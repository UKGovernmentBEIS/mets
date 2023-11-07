import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

import { DoalGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-preliminary-allocations',
  templateUrl: './preliminary-allocations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreliminaryAllocationsComponent {
  preliminaryAllocations$ = this.doalService.authorityPayload$.pipe(
    map((payload) => (payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse).preliminaryAllocations),
  );
  isEditable$ = this.doalService.isEditable$;

  constructor(
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../', 'approved-allocations'], { relativeTo: this.route });
  }
}
