import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { filter, map, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { GovukValidators } from 'govuk-components';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { applicationTypeMap } from './application-type';

@Component({
  selector: 'app-application-type',
  templateUrl: './application-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ApplicationTypeComponent implements OnInit {
  form: UntypedFormGroup = this.formBuilder.group({
    applicationType: [null, GovukValidators.required('Select application type')],
  });
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  options = applicationTypeMap;
  backLink = true;

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly pendingRequestService: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.store
      .getTask(ApplicationSectionType.applicationType)
      .pipe(
        filter((task) => !!task.value),
        tap((task) => this.form.patchValue(task.value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.store.updateTask(ApplicationSectionType.applicationType, this.form.value, 'complete');
    if (this.store.getState().isReviewed) {
      this.store
        .amend()
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => this.store.nextStep('../', this.route));
    } else {
      this.store.nextStep('../', this.route);
    }
  }
}
