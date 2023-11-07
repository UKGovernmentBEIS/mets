import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { MonitoringApproachCorsiaFormProvider } from '../monitoring-approach-form.provider';
import { MonitoringApproachTypeFormComponent } from '../monitoring-approach-type-form';

@Component({
  selector: 'app-monitoring-approach-type',
  templateUrl: './monitoring-approach-type.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    NgIf,
    NgFor,
    SharedModule,
    ReturnToLinkComponent,
    MonitoringApproachTypeFormComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MonitoringApproachTypeComponent implements OnInit {
  form = this.formProvider.monitoringApproachTypeForm;

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringApproachCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.formProvider.addCertEmissionsType();
  }

  onSubmit() {
    this.store.empCorsiaDelegate
      .saveEmp({ emissionsMonitoringApproach: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setEmissionsMonitoringApproach(this.formProvider.getFormValue());
        this.formProvider.removeCertEmissionsType();
        this.router.navigate(
          [
            this.formProvider.getFormValue()?.monitoringApproachType === 'CERT_MONITORING'
              ? 'simplified-approach'
              : 'summary',
          ],
          {
            relativeTo: this.route,
          },
        );
      });
  }
}
