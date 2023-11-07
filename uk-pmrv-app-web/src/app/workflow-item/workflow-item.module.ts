import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { DeleteRequestNoteComponent } from './notes/delete-note/delete-request-note.component';
import { RequestNoteComponent } from './notes/note/request-note.component';
import { RequestNotesComponent } from './notes/request-notes.component';
import { WorkflowRelatedCreateActionsComponent } from './shared/workflow-related-create-actions/workflow-related-create-actions.component';
import { WorkflowItemComponent } from './workflow-item.component';
import { WorkflowItemRoutingModule } from './workflow-item-routing.module';

@NgModule({
  declarations: [
    DeleteRequestNoteComponent,
    RequestNoteComponent,
    RequestNotesComponent,
    WorkflowItemComponent,
    WorkflowRelatedCreateActionsComponent,
  ],
  imports: [SharedModule, SharedUserModule, WorkflowItemRoutingModule],
})
export class WorkflowItemModule {}
