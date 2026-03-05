import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SectionComponent } from '@permit-application/shared/section/section.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Component({
  selector: 'app-connections',
  templateUrl: './connections.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConnectionsComponent extends SectionComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
    readonly router: Router,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
