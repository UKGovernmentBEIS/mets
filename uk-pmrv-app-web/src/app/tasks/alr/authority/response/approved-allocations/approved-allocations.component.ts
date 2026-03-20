import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { getTotalAllocationsPerYear } from '@shared/components/total-preliminary-allocation-list-template/total-preliminary-allocation-list.util';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityResponse, ALRPreliminaryAllocation } from 'pmrv-api';

import { alrApprovedAllocationsFormProvider } from './approved-allocations-form.provider';

@Component({
  selector: 'app-alr-approved-allocations',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
  templateUrl: './approved-allocations.component.html',
  providers: [alrApprovedAllocationsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrApprovedAllocationsComponent {
  preliminaryAllocations$ = this.alrService.authorityPayload$.pipe(
    map(
      (payload) =>
        (payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse)?.preliminaryAllocations,
    ),
  );
  isEditable$ = this.alrService.isEditable$;
  readonly documentsExist$ = this.form.get('documents').valueChanges.pipe(
    startWith(this.form.get('documents').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.alrService.authorityPayload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.alrService.postAlrAuthority(
            {
              authorityResponse: {
                ...payload.authorityReviewOutcome.authorityResponse,
                totalAllocationsPerYear: this.getTotalAllocationsPerYear(
                  (payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse)
                    ?.preliminaryAllocations ?? [],
                ),
                documents: this.form.value.documents?.map((file) => file.uuid),
              } as ALRGrantAuthorityResponse,
            },
            'authorityResponse',
            false,
            {
              ...this.form.value.documents?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate(['../', 'summary'], { relativeTo: this.route, state: { enableViewSummary: true } }),
      );
  }

  getBaseFileDownloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  private getTotalAllocationsPerYear(allocations: ALRPreliminaryAllocation[]): { [key: string]: number } {
    return getTotalAllocationsPerYear(allocations).reduce(function (map, obj) {
      map[obj.year] = obj.allowances;
      return map;
    }, {});
  }
}
