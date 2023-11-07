import { NgModule } from '@angular/core';

import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { SharedModule } from '../../shared/shared.module';
import { TaskSharedModule } from '../shared/task-shared-module';
import { DreRoutingModule } from './dre-routing.module';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { PeerReviewComponent } from './peer-review/peer-review.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { DreTaskComponent } from './shared/components/dre-task/dre-task.component';
import { ChargeOperatorComponent } from './submit/charge-operator/charge-operator.component';
import { DeterminationReasonComponent } from './submit/determination-reason/determination-reason.component';
import { FeeComponent } from './submit/fee/fee.component';
import { DeleteComponent } from './submit/information-sources/information-source/delete/delete.component';
import { InformationSourceComponent } from './submit/information-sources/information-source/information-source.component';
import { InformationSourcesComponent } from './submit/information-sources/information-sources.component';
import { MonitoringApproachesComponent } from './submit/monitoring-approaches/monitoring-approaches.component';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { OfficialNoticeReasonComponent } from './submit/official-notice-reason/official-notice-reason.component';
import { ReportableEmissionsComponent } from './submit/reportable-emissions/reportable-emissions.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';

@NgModule({
  declarations: [
    ChargeOperatorComponent,
    DeleteComponent,
    DeterminationReasonComponent,
    DreTaskComponent,
    FeeComponent,
    InformationSourceComponent,
    InformationSourcesComponent,
    InvalidDataComponent,
    MonitoringApproachesComponent,
    NotifyOperatorComponent,
    OfficialNoticeReasonComponent,
    PeerReviewComponent,
    PeerReviewWaitComponent,
    ReportableEmissionsComponent,
    SubmitContainerComponent,
    SummaryComponent,
  ],
  imports: [DreRoutingModule, SharedModule, TaskSharedModule],
  providers: [PeerReviewDecisionGuard],
})
export class DreModule {}
