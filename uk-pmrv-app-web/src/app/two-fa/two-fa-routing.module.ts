import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../core/guards/auth.guard';
import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { InvalidLinkComponent } from '../invitation/invalid-link/invalid-link.component';
import { Change2faComponent } from './change-2fa/change-2fa.component';
import { Delete2faComponent } from './delete-2fa/delete-2fa.component';
import { InvalidCodeComponent } from './invalid-code/invalid-code.component';
import { RequestTwoFaResetComponent } from './request-two-fa-reset/request-two-fa-reset.component';
import { ResetTwoFaComponent } from './reset-two-fa/reset-two-fa.component';

const routes: Routes = [
  {
    path: 'change',
    data: { pageTitle: 'Request to change two factor authentication' },
    component: Change2faComponent,
    canActivate: [AuthGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'invalid-code',
    data: { pageTitle: 'Invalid code' },
    canActivate: [AuthGuard],
    component: InvalidCodeComponent,
  },
  {
    path: 'request-change',
    data: { pageTitle: 'Request to change two factor authentication' },
    component: Delete2faComponent,
  },
  {
    path: 'invalid-link',
    data: { pageTitle: 'This link is invalid' },
    component: InvalidLinkComponent,
  },
  {
    path: 'request-2fa-reset',
    data: { pageTitle: 'Request two factor authentication reset' },
    component: RequestTwoFaResetComponent,
  },
  {
    path: 'reset-2fa',
    data: { pageTitle: 'Reset two factor authentication' },
    canActivate: [AuthGuard],
    component: ResetTwoFaComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TwoFaRoutingModule {}
