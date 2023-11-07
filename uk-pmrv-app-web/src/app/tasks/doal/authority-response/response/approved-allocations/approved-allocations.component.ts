import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { getTotalAllocationsPerYear } from '@shared/components/doal/total-preliminary-allocation-list-template/total-preliminary-allocation-list.util';
import { approvedAllocationsComponentFormProvider } from '@tasks/doal/authority-response/response/approved-allocations/approved-allocations.component-form.provider';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';

import { DoalGrantAuthorityResponse, PreliminaryAllocation } from 'pmrv-api';

@Component({
  selector: 'app-approved-allocations',
  templateUrl: './approved-allocations.component.html',
  providers: [approvedAllocationsComponentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApprovedAllocationsComponent {
  preliminaryAllocations$ = this.doalService.authorityPayload$.pipe(
    map((payload) => (payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse).preliminaryAllocations),
  );
  isEditable$ = this.doalService.isEditable$;
  readonly documentsExist$ = this.form.get('documents').valueChanges.pipe(
    startWith(this.form.get('documents').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.doalService.authorityPayload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.doalService.saveDoalAuthority(
            {
              authorityResponse: {
                ...payload.doalAuthority.authorityResponse,
                totalAllocationsPerYear: this.getTotalAllocationsPerYear(
                  (payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse)?.preliminaryAllocations ?? [],
                ),
                documents: this.form.value.documents?.map((file) => file.uuid),
              } as DoalGrantAuthorityResponse,
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
    return this.doalService.getBaseFileDownloadUrl();
  }

  private getTotalAllocationsPerYear(allocations: PreliminaryAllocation[]): { [key: string]: number } {
    return getTotalAllocationsPerYear(allocations).reduce(function (map, obj) {
      map[obj.year] = obj.allowances;
      return map;
    }, {});
  }
}
