import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';

import { AviationAerCorsiaAnnualOffsetting } from 'pmrv-api';

import { AnnualOffsettingRequirementsFormProvider } from '../../aer-corsia-annual-offsetting-form.provider';

@Component({
  selector: 'app-annual-offsetting-requirements',
  templateUrl: './offsetting-requirements.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnnualOffsettingRequirementsComponent implements OnInit {
  form = this.formProvider.form.get('offsettingRequirements') as FormGroup<
    Record<keyof AviationAerCorsiaAnnualOffsetting, FormControl>
  >;

  heading =
    'Input ' +
    this.form.value.schemeYear +
    ' total Chapter 3 State emissions and sector growth value to calculate the annual offsetting requirements';

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: AnnualOffsettingRequirementsFormProvider,
    private readonly store: RequestTaskStore,
    private readonly destroy$: DestroySubject,
    private readonly pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.form
      .get('totalChapter')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calculateAnnualOffsetting(this.form.value as AviationAerCorsiaAnnualOffsetting);
      });

    this.form
      .get('sectorGrowth')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.calculateAnnualOffsetting(this.form.value as AviationAerCorsiaAnnualOffsetting);
      });
  }

  onSubmit() {
    this.store.aerCorsiaAnnualOffsetting
      .saveOffsettingRequirement(this.form.value as AviationAerCorsiaAnnualOffsetting, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['summary'], {
          relativeTo: this.route,
        });
      });
  }

  calculateAnnualOffsetting(annualOffsetting: AviationAerCorsiaAnnualOffsetting) {
    const sectorGrowth = annualOffsetting.sectorGrowth;
    const totalChapter = annualOffsetting.totalChapter;

    if (
      totalChapter === null ||
      totalChapter.toString() === '' ||
      sectorGrowth === null ||
      sectorGrowth.toString() === ''
    ) {
      this.form.patchValue({ calculatedAnnualOffsetting: null });
    } else {
      this.form.patchValue({
        calculatedAnnualOffsetting: format(new BigNumber(+sectorGrowth).multipliedBy(+totalChapter).dividedBy(100), 0),
      });
    }

    this.form.get('calculatedAnnualOffsetting').updateValueAndValidity();
  }
}
