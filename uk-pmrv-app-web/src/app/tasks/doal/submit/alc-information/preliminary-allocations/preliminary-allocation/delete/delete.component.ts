import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-preliminary-allocation-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  index = this.route.snapshot.paramMap.get('index');
  editable$ = this.doalService.isEditable$;

  constructor(
    readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.doalService.payload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.doalService.saveDoal(
            {
              activityLevelChangeInformation: {
                ...payload.doal.activityLevelChangeInformation,
                preliminaryAllocations: payload.doal.activityLevelChangeInformation?.preliminaryAllocations?.filter(
                  (_, i) => i !== Number(this.index),
                ),
              },
            },
            this.route.snapshot.data.sectionKey,
            false,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
