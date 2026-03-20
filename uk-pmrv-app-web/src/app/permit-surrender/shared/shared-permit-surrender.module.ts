import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../../shared/shared.module';
import { PermitSurrenderSummaryComponent } from './permit-surrender-summary/permit-surrender-summary.component';

const declarations = [PermitSurrenderSummaryComponent];

@NgModule({
  imports: [RouterModule, SharedModule],
  declarations: declarations,
  exports: declarations,
})
export class SharedPermitSurrenderModule {}
