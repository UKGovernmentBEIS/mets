import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-emission-sources',
  templateUrl: './emission-sources.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourcesComponent extends SectionComponent implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus('emissionSources', true, data.permitTask)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateSubmitSection('summary', 'fuels'));
  }
}
