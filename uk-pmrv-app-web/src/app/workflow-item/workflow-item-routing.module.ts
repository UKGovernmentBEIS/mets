import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { NoteFileDownloadComponent } from '../shared/components/note-file-download/note-file-download.component';
import { DeleteRequestNoteComponent } from './notes/delete-note/delete-request-note.component';
import { RequestNoteComponent } from './notes/note/request-note.component';
import { WorkflowItemComponent } from './workflow-item.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Workflow item' },
    component: WorkflowItemComponent,
  },
  {
    path: 'notes',
    children: [
      {
        path: 'add',
        data: { pageTitle: 'Add a note', breadcrumb: true },
        component: RequestNoteComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':noteId/edit',
        data: { pageTitle: 'Edit a note', breadcrumb: true },
        component: RequestNoteComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':noteId/delete',
        data: { pageTitle: 'Delete a note', breadcrumb: true },
        component: DeleteRequestNoteComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'file-download/:uuid',
    component: NoteFileDownloadComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WorkflowItemRoutingModule {}
