import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable()
export class ComplianceEtsService implements PendingRequest {
  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  get isEditable$(): Observable<boolean> {
    return this.aerService.isEditable$;
  }

  onSubmit(nextRoute: string, isDirty: boolean, formData: Record<string, unknown>): void {
    if (!isDirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getPayload()
        .pipe(
          first(),
          map(
            (payload) =>
              (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.etsComplianceRules,
          ),
          switchMap((estComplianceRulesData) =>
            this.aerService.postVerificationTaskSave(
              {
                etsComplianceRules: {
                  ...estComplianceRulesData,
                  ...formData,
                },
              },
              false,
              'etsComplianceRules',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
