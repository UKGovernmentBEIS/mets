import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  installationActivities,
  InstallationActivity,
  subCategoryInstallationActivityMap,
} from '@tasks/aer/submit/nace-codes/nace-code-types';
import { naceCodeInstallationActivityFormProvider } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity-form.provider';

@Component({
  selector: 'app-nace-code-installation-activity',
  templateUrl: './nace-code-installation-activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [naceCodeInstallationActivityFormProvider],
})
export class NaceCodeInstallationActivityComponent implements OnInit {
  installationActivityOptions: [string, string | any][];
  readonly subCategory = this.route.snapshot?.queryParamMap.get('subCategory');

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    const relevantInstallationActivities = Object.entries(subCategoryInstallationActivityMap).find(
      (entry) => entry[0] === this.subCategory,
    )[1];
    this.installationActivityOptions = Object.entries(installationActivities).filter((entry) =>
      relevantInstallationActivities.includes(entry[0] as InstallationActivity),
    );
    this.enableOptionalFields();
  }

  onSubmit(): void {
    const activity =
      (this.form.value.installationActivityChild0 ||
        this.form.value.installationActivityChild1 ||
        this.form.value.installationActivityChild2 ||
        this.form.value.installationActivityChild3 ||
        this.form.value.installationActivityChild4 ||
        this.form.value.installationActivityChild5 ||
        this.form.value.installationActivityChild6 ||
        this.form.value.installationActivityChild7 ||
        this.form.value.installationActivityChild8 ||
        this.form.value.installationActivityChild9) ??
      this.form.value.installationActivity;

    this.aerService
      .getTask('naceCodes')
      .pipe(
        first(),
        switchMap((naceCodes) =>
          this.aerService.postTaskSave(
            {
              naceCodes: {
                codes: (naceCodes?.codes ?? []).concat(activity),
              },
            },
            undefined,
            false,
            'naceCodes',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  // same solution as DecisionComponent. The bug is probably in the conditional component and we should fix it there.
  private enableOptionalFields() {
    this.form
      .get('installationActivity')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('installationActivity')) {
          Object.keys(this.form.controls).forEach((key) => {
            if (key.startsWith('installationActivityChild')) {
              this.form.get(key).setValue(null);
              this.form.get(key).enable();
            }
          });
        }
      });
  }
}
