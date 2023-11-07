import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseMonitoringApproachComponent } from '../base-monitoring-approach.component';
import { MonitoringApproachTotalEmissionsFormComponent } from '../monitoring-approach-total-emissions-form';

@Component({
  selector: 'app-monitoring-approach-total-emissions',
  templateUrl: './monitoring-approach-total-emissions.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    NgIf,
    NgFor,
    SharedModule,
    ReturnToLinkComponent,
    MonitoringApproachTotalEmissionsFormComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MonitoringApproachTotalEmissionsComponent
  extends BaseMonitoringApproachComponent
  implements OnInit, OnDestroy
{
  form = this.formProvider.aviationAerSupportFacilityMonitoringApproachCtrl;

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    this.saveEmpAndNavigate(this.formProvider.getFormValue(), 'in progress', '../summary');
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
