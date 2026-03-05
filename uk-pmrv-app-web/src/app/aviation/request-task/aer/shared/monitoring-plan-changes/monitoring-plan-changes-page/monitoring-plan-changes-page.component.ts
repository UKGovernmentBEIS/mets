import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerMonitoringPlanVersion } from 'pmrv-api';

import { MonitoringPlanChangesFormComponent } from '../monitoring-plan-changes-form';
import { MonitoringPlanChangesFormProvider } from '../monitoring-plan-changes-form.provider';

@Component({
  selector: 'app-monitoring-plan-changes-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    MonitoringPlanChangesFormComponent,
    ReturnToLinkComponent,
    AerMonitoringPlanVersionsComponent,
  ],
  templateUrl: './monitoring-plan-changes-page.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanChangesPageComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;
  planVersions$: Observable<AviationAerMonitoringPlanVersion[]>;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringPlanChangesFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.planVersions$ = this.store.pipe(aerQuery.selectAerMonitoringPlanVersions);
  }

  onSubmit() {
    this.store.aerDelegate
      .saveAer({ aerMonitoringPlanChanges: this.form.value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setMonitoringPlanChanges(this.form.value);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
