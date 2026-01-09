import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewSubmittedComponent } from '@shared/components/peer-review-decision/timeline/peer-review-submitted.component';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PermitRoute } from '../permit-route.interface';
import { AdditionalInfoComponent } from './additional-info/additional-info.component';
import { ConfirmationComponent as ReturnForAmendsConfirmationComponent } from './amend/return-for-amends/confirmation/confirmation.component';
import { ReturnForAmendsComponent } from './amend/return-for-amends/return-for-amends.component';
import { ReturnForAmendsGuard } from './amend/return-for-amends/return-for-amends.guard';
import { CalculationComponent } from './calculation/calculation.component';
import { ConfidentialityComponent } from './confidentiality/confidentiality.component';
import { DetailsComponent } from './details/details.component';
import { ActivationDateComponent } from './determination/activation-date/activation-date.component';
import { ActivationDateGuard } from './determination/activation-date/activation-date.guard';
import { AnswersComponent } from './determination/answers/answers.component';
import { AnswersGuard } from './determination/answers/answers.guard';
import { EmissionsComponent } from './determination/emissions/emissions.component';
import { EmissionsGuard } from './determination/emissions/emissions.guard';
import { OfficialNoticeComponent } from './determination/official-notice/official-notice.component';
import { OfficialNoticeGuard } from './determination/official-notice/official-notice.guard';
import { ReasonComponent } from './determination/reason/reason.component';
import { ReasonGuard } from './determination/reason/reason.guard';
import { SummaryComponent } from './determination/summary/summary.component';
import { SummaryGuard } from './determination/summary/summary.guard';
import { FallbackComponent } from './fallback/fallback.component';
import { FuelsComponent } from './fuels/fuels.component';
import { InherentCO2Component } from './inherent-co2/inherent-co2.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { MeasurementComponent } from './measurement/measurement.component';
import { MonitoringApproachesComponent } from './monitoring-approaches/monitoring-approaches.component';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { NitrousOxideComponent } from './nitrous-oxide/nitrous-oxide.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './notify-operator/notify-operator.guard';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PfcComponent } from './pfc/pfc.component';
import { ConfirmationComponent as RecallConfirmationComponent } from './recall/confirmation/confirmation.component';
import { RecallComponent } from './recall/recall.component';
import { RecallGuard } from './recall/recall.guard';
import { TransferredCO2Component } from './transferred-co2/transferred-co2.component';
import { UncertaintyAnalysisComponent } from './uncertainty-analysis/uncertainty-analysis.component';

export const routes: PermitRoute[] = [
  {
    path: 'permit-type',
    data: { pageTitle: 'Permit type', groupKey: 'PERMIT_TYPE', breadcrumb: true },
    component: PermitTypeComponent,
  },
  {
    path: 'details',
    data: { pageTitle: 'Installation details', groupKey: 'INSTALLATION_DETAILS', breadcrumb: true },
    component: DetailsComponent,
  },
  {
    path: 'fuels',
    data: { pageTitle: 'Fuels and equipment inventory', groupKey: 'FUELS_AND_EQUIPMENT', breadcrumb: true },
    component: FuelsComponent,
  },
  {
    path: 'monitoring-approaches',
    data: { pageTitle: 'Define monitoring approaches', groupKey: 'DEFINE_MONITORING_APPROACHES', breadcrumb: true },
    component: MonitoringApproachesComponent,
  },
  {
    path: 'nitrous-oxide',
    data: { pageTitle: 'Measurement of nitrous oxide (N2O)', groupKey: 'MEASUREMENT_N2O', breadcrumb: true },
    component: NitrousOxideComponent,
  },
  {
    path: 'pfc',
    data: { pageTitle: 'Calculation of perfluorocarbons (PFC)', groupKey: 'CALCULATION_PFC', breadcrumb: true },
    component: PfcComponent,
  },
  {
    path: 'inherent-co2',
    data: { pageTitle: 'Inherent CO2', groupKey: 'INHERENT_CO2', breadcrumb: true },
    component: InherentCO2Component,
  },
  {
    path: 'transferred-co2',
    data: { pageTitle: 'Procedures for transferred CO2 or N2O', groupKey: 'TRANSFERRED_CO2_N2O', breadcrumb: true },
    component: TransferredCO2Component,
  },
  {
    path: 'management-procedures',
    data: { pageTitle: 'Management procedures', groupKey: 'MANAGEMENT_PROCEDURES', breadcrumb: true },
    component: ManagementProceduresComponent,
  },
  {
    path: 'monitoring-methodology-plan',
    data: { pageTitle: 'Monitoring methodology plan', groupKey: 'MONITORING_METHODOLOGY_PLAN', breadcrumb: true },
    component: MonitoringMethodologyPlanComponent,
  },
  {
    path: 'additional-info',
    data: { pageTitle: 'Additional information', groupKey: 'ADDITIONAL_INFORMATION', breadcrumb: true },
    component: AdditionalInfoComponent,
  },
  {
    path: 'confidentiality',
    data: { pageTitle: 'Confidentiality statement', groupKey: 'CONFIDENTIALITY_STATEMENT', breadcrumb: true },
    component: ConfidentialityComponent,
  },
  {
    path: 'measurement',
    data: { pageTitle: 'Measurement of CO2', groupKey: 'MEASUREMENT_CO2', breadcrumb: true },
    component: MeasurementComponent,
  },
  {
    path: 'fall-back',
    data: { pageTitle: 'Fallback approach', groupKey: 'FALLBACK', breadcrumb: true },
    component: FallbackComponent,
  },
  {
    path: 'calculation',
    data: { pageTitle: 'Calculation of CO2', groupKey: 'CALCULATION_CO2', breadcrumb: true },
    component: CalculationComponent,
  },
  {
    path: 'uncertainty-analysis',
    data: { pageTitle: 'Uncertainty analysis', groupKey: 'UNCERTAINTY_ANALYSIS', breadcrumb: true },
    component: UncertaintyAnalysisComponent,
  },
  {
    path: 'determination',
    data: { statusKey: 'determination' },
    children: [
      {
        path: 'reason',
        data: { pageTitle: 'Overall decision reason' },
        component: ReasonComponent,
        canActivate: [ReasonGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'activation-date',
        data: { pageTitle: 'Overall decision activation date' },
        component: ActivationDateComponent,
        canActivate: [ActivationDateGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'official-notice',
        data: { pageTitle: 'Overall decision official notice' },
        component: OfficialNoticeComponent,
        canActivate: [OfficialNoticeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'emissions',
        data: { pageTitle: 'Overall decision emissions' },
        component: EmissionsComponent,
        canActivate: [EmissionsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Overall decision - Confirm Answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Overall decision summary', breadcrumb: true },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Notify operator' },
    component: NotifyOperatorComponent,
    canActivate: [NotifyOperatorGuard, PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'peer-review',
    data: { pageTitle: 'Peer review' },
    component: PeerReviewComponent,
    canActivate: [PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'peer-review-decision',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review decision' },
        component: PeerReviewDecisionComponent,
        canActivate: [PeerReviewDecisionGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Peer review decision answers' },
        component: PeerReviewDecisionAnswersComponent,
        canActivate: [PeerReviewDecisionAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Peer review decision confirmation' },
        component: PeerReviewDecisionConfirmationComponent,
      },
    ],
  },
  {
    path: 'peer-reviewer-submitted',
    data: { pageTitle: 'Peer review submitted' },
    component: PeerReviewSubmittedComponent,
  },
  {
    path: 'return-for-amends',
    children: [
      {
        path: '',
        data: { pageTitle: 'Return for amends' },
        component: ReturnForAmendsComponent,
        canActivate: [ReturnForAmendsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Return for amends confirmation' },
        component: ReturnForAmendsConfirmationComponent,
      },
    ],
  },
  {
    path: 'recall-from-amends',
    children: [
      {
        path: '',
        data: { pageTitle: 'Recall from amends' },
        component: RecallComponent,
        canActivate: [RecallGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Recall from amends confirmation' },
        component: RecallConfirmationComponent,
      },
    ],
  },
  {
    path: 'payment-not-completed',
    component: PaymentNotCompletedComponent,
  },
  {
    path: 'tasks',
    loadChildren: () => import('./../../tasks/tasks.module').then((m) => m.TasksModule),
  },
  {
    path: 'rfi',
    loadChildren: () => import('../../rfi/rfi.module').then((m) => m.RfiModule),
  },
  {
    path: 'rde',
    loadChildren: () => import('../../rde/rde.module').then((m) => m.RdeModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReviewRoutingModule {}
