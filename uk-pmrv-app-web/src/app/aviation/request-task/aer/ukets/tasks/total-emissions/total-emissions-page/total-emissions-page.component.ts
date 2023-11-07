import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TotalEmissionsSchemeYearHeaderComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/shared/total-emissions-scheme-year-header';
import { TotalEmissionsStandardFuelsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-standard-fuels-table';
import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { RequestTaskModule } from '@aviation/request-task/request-task.module';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerEmissionsCalculationDTO } from 'pmrv-api';

@Component({
  selector: 'app-total-emissions-page',
  templateUrl: './total-emissions-page.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    AsyncPipe,
    NgIf,
    SharedModule,
    ReturnToLinkComponent,
    RequestTaskModule,
    TotalEmissionsStandardFuelsTableComponent,
    TotalEmissionsSchemeYearHeaderComponent,
    TotalEmissionsSchemeYearHeaderComponent,
  ],
})
export class TotalEmissionsPageComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: TotalEmissionsFormProvider,
    private router: Router,
    private readonly route: ActivatedRoute,
    private requestTaskStore: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
  ) {}

  form = this.formProvider.form;
  aviationAerEmissionsCalculationDTO: AviationAerEmissionsCalculationDTO;

  onSubmit() {
    this.requestTaskStore.aerDelegate
      .saveAer({ aviationAerTotalEmissionsConfidentiality: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.requestTaskStore.aerDelegate.setTotalEmissions(this.form.value);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
