import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InstallationInspectionOperatorRespondRequestTaskPayload } from 'pmrv-api';

import { InspectionService } from '../core/inspection.service';

export abstract class FollowUpRespondAbstractComponent {
  actionId = +this.route.snapshot.paramMap.get('actionId');
  payload$: Observable<InstallationInspectionOperatorRespondRequestTaskPayload> = this.inspectionService.payload$;

  isEditable = toSignal(this.inspectionService.isEditable$);
  payload = toSignal(this.payload$);
  attachments = toSignal(
    this.payload$.pipe(
      map((payload) =>
        payload?.inspectionAttachments
          ? payload.installationInspection.followUpActions.map((val) => {
              return this.inspectionService.getOperatorDownloadUrlFiles(val.followUpActionAttachments);
            })
          : [],
      ),
    ),
  );

  constructor(
    protected readonly route: ActivatedRoute,
    protected readonly inspectionService: InspectionService,
  ) {}
}
