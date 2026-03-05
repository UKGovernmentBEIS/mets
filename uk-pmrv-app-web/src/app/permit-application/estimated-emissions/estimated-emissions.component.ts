import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { InstallationCategoryPipe } from '../shared/pipes/installation-category.pipe';
import { InstallationCategoryTypePipe } from '../shared/pipes/installation-category-type.pipe';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { estimatedEmissionsFormProvider } from './estimated-emissions-form.provider';

@Component({
  selector: 'app-estimated-emissions',
  templateUrl: './estimated-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, estimatedEmissionsFormProvider],
})
export class EstimatedEmissionsComponent extends SectionComponent implements OnInit, PendingRequest {
  category$ = new BehaviorSubject<string>(null);

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {
    super(store, router, route);
  }

  ngOnInit(): void {
    this.form
      .get('quantity')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((res) => {
        const installationCategoryType = new InstallationCategoryTypePipe();
        const categoryType = installationCategoryType.transform(Number(res));

        const installationCategory = new InstallationCategoryPipe();
        const category = installationCategory.transform(categoryType);

        this.category$.next(category);
      });
  }

  onSubmit(): void {
    this.store
      .postTask('estimatedAnnualEmissions', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'details'));
  }
}
