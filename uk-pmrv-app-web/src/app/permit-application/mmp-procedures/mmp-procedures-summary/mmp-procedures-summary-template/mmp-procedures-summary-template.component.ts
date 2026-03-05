import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SnakeToKebabPipe } from '@shared/pipes/snake-to-kebab.pipe';
import { SharedModule } from '@shared/shared.module';

import { procedureTypes } from '../../mmp-procedures-status';

@Component({
  selector: 'app-mmp-procedures-summary-template',
  templateUrl: './mmp-procedures-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedPermitModule, SharedModule, RouterLink],
})
export class MmpProceduresSummaryTemplateComponent extends ProductBenchmarkComponent implements PendingRequest {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;
  @Input() isEditable = true;

  procedureTypes = procedureTypes;

  labelsForHeader = {
    ASSIGNMENT_OF_RESPONSIBILITIES: 'Assignment of responsibilities',
    MONITORING_PLAN_APPROPRIATENESS: 'Monitoring plan appropriateness',
    DATA_FLOW_ACTIVITIES: 'Data flow activities',
    CONTROL_ACTIVITIES: 'Control activities',
  };

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  routerLink(procedureType): string {
    const pipe = new SnakeToKebabPipe();
    if (procedureType === 'ASSIGNMENT_OF_RESPONSIBILITIES') {
      return '../';
    }
    return '../' + pipe.transform(procedureType);
  }
}
