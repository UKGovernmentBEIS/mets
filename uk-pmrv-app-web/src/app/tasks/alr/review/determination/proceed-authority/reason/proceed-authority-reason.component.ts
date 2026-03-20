import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  alrArticleReasonGroupTypeLabelsMap,
  alrArticleReasonItemsLabelsMap,
} from '@shared/components/alr/alr-determination-proceed-authority.label.map';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { alrProceedAuthorityReasonFormProvider } from './proceed-authority-reason-form.provider';

@Component({
  selector: 'app-alr-proceed-authority-reason',
  imports: [SharedModule, AlrTaskSharedModule],
  templateUrl: './proceed-authority-reason.component.html',
  providers: [alrProceedAuthorityReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrProceedAuthorityReasonComponent implements OnInit {
  isEditable = this.alrService.isEditable;

  articleReasonGroupTypeLabelsMap = alrArticleReasonGroupTypeLabelsMap;
  articleReasonItemsLabelsMap = alrArticleReasonItemsLabelsMap;

  article6AReasons: DoalProceedToAuthorityDetermination['articleReasonItems'][number][] = [
    'ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5',
    'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
    'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
    'ADJUSTMENT_OF_PARAMETERS_OTHER_THAN_ACTIVITY_LEVEL',
    'TEMPORARY_CESSATION',
    'PERMANENT_CESSATION',
  ];

  article34HReasons: DoalProceedToAuthorityDetermination['articleReasonItems'][number][] = [
    'ERROR_IN_BASELINE_DATA_REPORT',
    'ERROR_IN_NEW_ENTRANT_DATA_REPORT',
    'ERROR_IN_ACTIVITY_LEVEL_REPORT',
    'ERROR_MADE_BY_REGULATOR_OR_AUTHORITY',
  ];

  ngOnInit(): void {
    this.form
      .get('articleReasonGroupType')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value === 'ARTICLE_6A_REASONS') {
          this.form.get('article6aReasons').setValue(null);
          this.form.get('article6aReasons').enable();
        } else {
          this.form.get('article34HReasonItems').setValue(null);
          this.form.get('article34HReasonItems').enable();
        }
      });
  }

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit(): void {
    const nextWizardStep = ['../', 'withholding-of-allowances'];

    if (!this.form.dirty) {
      this.router.navigate(nextWizardStep, {
        relativeTo: this.route,
      });
    } else {
      this.alrService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.alrService.postAlrReview(
              {
                ...payload.regulatorReviewOutcome,
                determination: {
                  ...payload.regulatorReviewOutcome.determination,
                  reason: this.form.value.reason,
                  articleReasonGroupType: this.form.value.articleReasonGroupType,
                  articleReasonItems:
                    this.form.value.articleReasonGroupType === 'ARTICLE_6A_REASONS'
                      ? this.form.value.article6aReasons
                      : this.form.value.article34HReasonItems,
                },
              },
              'DETERMINATION',
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(nextWizardStep, {
            relativeTo: this.route,
          }),
        );
    }
  }
}
