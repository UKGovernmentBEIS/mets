import { NgFor, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { MonitoringApproachCorsiaValues } from '@aviation/request-task/emp/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { MonitoringApproachTypeCorsiaPipe } from '@aviation/shared/pipes/monitoring-approach-type-corsia.pipe';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-monitoring-approach-summary-template',
  templateUrl: './monitoring-approach-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, GovukComponentsModule, RouterLinkWithHref, NgFor, NgForOf, MonitoringApproachTypeCorsiaPipe],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: MonitoringApproachCorsiaValues;
  @Input() files: { fileName: string; downloadUrl: string }[] = [];
}
