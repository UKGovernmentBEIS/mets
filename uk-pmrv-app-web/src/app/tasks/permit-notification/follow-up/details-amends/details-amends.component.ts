import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { PermitNotificationService } from '@tasks/permit-notification/core/permit-notification.service';

import { GovukValidators } from 'govuk-components';

import {
  PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
  PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-details-amends',
  templateUrl: './details-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsAmendsComponent implements OnInit {
  isEditable$ = this.permitNotificationService.getIsEditable();
  showForm$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup = this.fb.group({
    changes: [null, GovukValidators.required('Check the box to confirm you have made changes')],
  });

  data$ = this.permitNotificationService.getPayload().pipe(
    first(),
    tap((payload) => this.showForm$.next(!payload.followUpSectionsCompleted?.AMENDS_NEEDED)),
    map((payload) => ({
      ...payload.reviewDecision,
      details: {
        ...payload.reviewDecision?.details,
        dueDate: payload.reviewDecision?.details?.dueDate ?? payload.followUpResponseExpirationDate,
      },
    })),
  );

  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    public readonly permitNotificationService: PermitNotificationService,
    readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    private readonly backLinkService: BackLinkService,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  submit() {
    if (this.form.valid) {
      const sectionsCompleted = this.form.get('changes').value;

      this.permitNotificationService
        .getPayload()
        .pipe(
          first(),
          switchMap((payload) => {
            const taskPayload = payload as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
            const attachments = taskPayload.followUpAttachments;
            const taskActionPayload: PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload = {
              response: taskPayload.followUpResponse,
              files: taskPayload.followUpFiles?.length > 0 ? taskPayload.followUpFiles : [],
              followUpSectionsCompleted: { ['AMENDS_NEEDED']: sectionsCompleted[0] },
              reviewSectionsCompleted: taskPayload.reviewSectionsCompleted,
            };

            return this.permitNotificationService.postSubmit(
              'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND',
              'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD',
              taskActionPayload,
              attachments,
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.showForm$.next(false));
    } else {
      this.displayErrorSummary$.next(true);
    }
  }
}
