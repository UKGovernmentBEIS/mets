import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { SharedUserModule } from '../shared-user/shared-user.module';
import { AddComponent } from './add/add.component';
import { AddComponent as ContactsAddComponent } from './contacts/add/add.component';
import { ContactsComponent } from './contacts/contacts.component';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { FormComponent } from './form/form.component';
import { VerificationBodiesComponent } from './verification-bodies.component';
import { VerificationBodiesRoutingModule } from './verification-bodies-routing.module';

@NgModule({
  imports: [SharedModule, SharedUserModule, VerificationBodiesRoutingModule],
  declarations: [
    AddComponent,
    ContactsAddComponent,
    ContactsComponent,
    DeleteComponent,
    DetailsComponent,
    FormComponent,
    VerificationBodiesComponent,
  ],
})
export class VerificationBodiesModule {}
