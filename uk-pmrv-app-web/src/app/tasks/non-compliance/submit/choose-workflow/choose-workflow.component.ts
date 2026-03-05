import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectCurrentDomain } from '@core/store';
import { AuthStore } from '@core/store/auth/auth.store';

import { NonComplianceApplicationSubmitRequestTaskPayload, RequestInfoDTO } from 'pmrv-api';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-choose-workflow',
  templateUrl: './choose-workflow.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChooseWorkflowComponent implements OnInit {
  private readonly nextWizardStep = 'civil-penalty';

  form: UntypedFormGroup = this.fb.group({});

  selectedRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.selectedRequests || []),
  );

  availableRequests$ = this.nonComplianceService.payload$.pipe(
    map((payload) => (payload as NonComplianceApplicationSubmitRequestTaskPayload)?.availableRequests),
  );

  requests: Array<RequestInfoDTO>;

  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    readonly nonComplianceService: NonComplianceService,
    private readonly fb: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.selectedRequests$, this.availableRequests$])
      .pipe(
        first(),
        switchMap(([selectedRequests, availableRequests]) => {
          return (this.requests = availableRequests.filter((a) => selectedRequests.some((b) => b === a.id)));
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
  }
}
