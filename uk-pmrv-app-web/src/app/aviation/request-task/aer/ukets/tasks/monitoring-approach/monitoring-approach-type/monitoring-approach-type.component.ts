import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BaseMonitoringApproachComponent } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach/base-monitoring-approach.component';
import { MonitoringApproachTypeFormComponent } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach/monitoring-approach-type-form';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

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
export class MonitoringApproachTypeComponent extends BaseMonitoringApproachComponent implements OnInit, OnDestroy {
  form = new FormGroup({ monitoringApproachType: this.formProvider.monitoringApproachTypeCtrl });

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    const data = {
      ...this.formProvider.getFormValue(),
      ...this.form.value,
    } as any;
    this.formProvider.setFormValue(data);
    let navigationUrl = '';
    let typeHasChanged = false;

    if (this.form.value?.monitoringApproachType === 'EUROCONTROL_SUPPORT_FACILITY') {
      navigationUrl = 'scheme-year-total-emissions';
      this.formProvider.addAviationAerSupportFacilityMonitoringApproach();
      this.formProvider.removeAviationAerSmallEmittersMonitoringApproach();
      if (data?.totalEmissions) typeHasChanged = true;
    } else if (this.form.value?.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS') {
      navigationUrl = 'total-number-full-scope-flights';
      this.formProvider.addAviationAerSmallEmittersMonitoringApproach();
      this.formProvider.removeAviationAerSupportFacilityMonitoringApproach();
      if (data?.totalEmissionsType) typeHasChanged = true;
    } else {
      navigationUrl = 'summary';
      this.formProvider.removeAviationAerSupportFacilityMonitoringApproach();
      this.formProvider.removeAviationAerSmallEmittersMonitoringApproach();
      if (data?.totalEmissionsType || data?.totalEmissions) typeHasChanged = true;
    }

    this.saveEmpAndNavigate(typeHasChanged ? this.form.value : data, 'in progress', navigationUrl);
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
