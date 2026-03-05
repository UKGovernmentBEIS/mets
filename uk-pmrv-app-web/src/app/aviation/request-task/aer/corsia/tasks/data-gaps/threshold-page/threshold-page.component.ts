import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaDataGapsDetails } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';

@Component({
  selector: 'app-threshold-page',
  templateUrl: './threshold-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, ThresholdPageComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ThresholdPageComponent {
  protected form = new FormGroup({
    dataGapsPercentageType: this.formProvider.dataGapsPercentageTypeCtrl,
    dataGapsPercentage: this.formProvider.dataGapsPercentageCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    this.store.aerDelegate
      .saveAer(
        {
          dataGaps: {
            ...this.formProvider.form.value,
            dataGapsDetails: { ...(this.form.value as AviationAerCorsiaDataGapsDetails) },
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        let nextRoute = '../data-gaps-list/add-data-gap-information';
        if (this.form.value.dataGapsPercentageType === 'LESS_EQUAL_FIVE_PER_CENT') {
          nextRoute = '../summary';
        }

        this.router.navigate([nextRoute], { relativeTo: this.route });
      });
  }
}
