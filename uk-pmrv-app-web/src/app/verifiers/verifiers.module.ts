import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { SiteContactsComponent } from './site-contacts/site-contacts.component';
import { VerifiersComponent } from './verifiers.component';
import { VerifiersRoutingModule } from './verifiers-routing.module';

@NgModule({
  imports: [SharedModule, SharedUserModule, VerifiersRoutingModule],
  declarations: [DeleteComponent, DetailsComponent, SiteContactsComponent, VerifiersComponent],
})
export class VerifiersModule {}
