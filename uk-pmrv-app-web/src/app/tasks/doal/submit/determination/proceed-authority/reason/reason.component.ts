import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap, takeUntil } from 'rxjs';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../../../core/services/destroy-subject.service';
import {
  articleReasonGroupTypeLabelsMap,
  articleReasonItemsLabelsMap,
} from '../../../../../../shared/components/doal/determination-proceed-authority.label.map';
import { DoalService } from '../../../../core/doal.service';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';
import { reasonFormProvider } from './reason-form.provider';

@Component({
  selector: 'app-proceed-authority-reason',
  templateUrl: './reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonFormProvider],
})
export class ReasonComponent implements OnInit {
  private readonly nextWizardStep = 'withholding';
  editable$: Observable<boolean> = this.doalService.isEditable$;

  articleReasonGroupTypeLabelsMap = articleReasonGroupTypeLabelsMap;
  articleReasonItemsLabelsMap = articleReasonItemsLabelsMap;

  article6AReasons: DoalProceedToAuthorityDetermination['articleReasonItems'][number][] = [
    'ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5',
    'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
    'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
    'ADJUSTMENT_OF_PARAMETERS_OTHER_THAN_ACTIVITY_LEVEL',
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
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../', this.nextWizardStep], {
        relativeTo: this.route,
      });
    } else {
      this.doalService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoal(
              {
                determination: {
                  ...payload.doal.determination,
                  reason: this.form.value.reason,
                  articleReasonGroupType: this.form.value.articleReasonGroupType,
                  articleReasonItems:
                    this.form.value.articleReasonGroupType === 'ARTICLE_6A_REASONS'
                      ? this.form.value.article6aReasons
                      : this.form.value.article34HReasonItems,
                } as any,
              },
              this.route.snapshot.data.sectionKey,
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate(['../', this.nextWizardStep], {
            relativeTo: this.route,
          }),
        );
    }
  }
}
