import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DescriptionComponent } from '@tasks/aer/submit/fallback/description/description.component';
import { FallbackComponent } from '@tasks/aer/submit/fallback/fallback.component';
import { SummaryGuard } from '@tasks/aer/submit/fallback/guards/summary.guard';
import { WizardStepGuard } from '@tasks/aer/submit/fallback/guards/wizard-step.guard';
import { SummaryComponent } from '@tasks/aer/submit/fallback/summary/summary.component';
import { TotalEmissionsComponent } from '@tasks/aer/submit/fallback/total-emissions/total-emissions.component';
import { UploadDocumentsComponent } from '@tasks/aer/submit/fallback/upload-documents/upload-documents.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Fallback - Emission network' },
    component: FallbackComponent,
    canActivate: [WizardStepGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'description',
    data: { pageTitle: 'Monitoring approaches - Fallback - Description', backlink: '../' },
    component: DescriptionComponent,
    canActivate: [WizardStepGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'total-emissions',
    data: { pageTitle: 'Monitoring approaches - Fallback - Total emissions', backlink: '../description' },
    component: TotalEmissionsComponent,
    canActivate: [WizardStepGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'upload-documents',
    data: { pageTitle: 'Monitoring approaches - Fallback - Upload documents', backlink: '../total-emissions' },
    component: UploadDocumentsComponent,
    canActivate: [WizardStepGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: {
      pageTitle: 'Monitoring approaches - Fallback - Summary',
      breadcrumb: 'Fallback summary',
    },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FallbackRoutingModule {}
