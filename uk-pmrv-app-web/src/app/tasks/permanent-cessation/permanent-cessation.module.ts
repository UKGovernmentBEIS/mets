import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { PermanentCessationRoutingModule } from './permanent-cessation-routing.module';
import { PermanentCessationTaskSharedModule } from './shared';
import { SubmitContainerComponent } from './submit/submit-container.component';

@NgModule({
  imports: [
    PermanentCessationRoutingModule,
    PermanentCessationTaskSharedModule,
    SharedModule,
    SubmitContainerComponent,
    TaskSharedModule,
  ],
  providers: [],
})
export class PermanentCessationModule {}
