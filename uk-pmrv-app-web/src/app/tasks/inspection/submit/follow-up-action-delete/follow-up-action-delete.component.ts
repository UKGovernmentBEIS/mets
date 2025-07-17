import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { InspectionSaveRequestTaskActionPayload, InspectionType } from '@tasks/inspection/shared/inspection';
import { followUpStatusKeySubmit } from '@tasks/inspection/shared/section-status';

@Component({
  selector: 'app-follow-up-action-delete',
  standalone: true,
  imports: [SharedModule, RouterLink],
  template: `
    <div class="govuk-!-width-two-thirds">
      <app-page-heading size="l">Are you sure you want to remove follow-up action?</app-page-heading>
      <p class="govuk-body">Any reference to this item will be removed from your application.</p>
      <div class="govuk-button-group">
        <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
        <a routerLink="../../follow-up-actions" govukLink>Cancel</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpActionDeleteComponent {
  reference = +this.route.snapshot.paramMap.get('id');
  inspectionPayload$: Observable<InspectionSaveRequestTaskActionPayload> = this.inspectionService.payload$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly inspectionService: InspectionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  delete() {
    this.inspectionPayload$
      .pipe(
        first(),
        switchMap((payload) => {
          const newFollowUpActions = payload.installationInspection?.followUpActions.filter(
            (_, i) => i !== this.reference,
          );

          return this.inspectionService.postInspectionTaskSave({
            installationInspection: {
              ...payload?.installationInspection,
              followUpActions: newFollowUpActions,
            },
            installationInspectionSectionsCompleted: {
              ...payload?.installationInspectionSectionsCompleted,
              [followUpStatusKeySubmit]: false,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../', 'follow-up-actions'], { relativeTo: this.route }));
  }
}
