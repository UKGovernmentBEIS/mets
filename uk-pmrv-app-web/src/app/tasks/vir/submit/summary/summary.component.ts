import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  operatorImprovementResponse$ = this.virService.payload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virService.payload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );
  isEditable$ = this.virService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.virService.payload$
      .pipe(
        first(),
        switchMap((payload) => {
          return this.virService.postVirTaskSave({
            operatorImprovementResponses: payload?.operatorImprovementResponses,
            virSectionsCompleted: {
              ...payload?.virSectionsCompleted,
              [this.reference]: true,
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
