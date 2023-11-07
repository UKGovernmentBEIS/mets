import { NgModule } from '@angular/core';

import { NoteFileDownloadComponent } from '@shared/components/note-file-download/note-file-download.component';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { SharedModule } from '@shared/shared.module';

import { legalEntityFormFactory } from '../installation-account-application/factories/legal-entity-form.factory';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountComponent } from './account.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { DetailsComponent } from './details/details.component';
import { AddressComponent } from './edit/address/address.component';
import { FaStatusComponent } from './edit/fa-status/fa-status.component';
import { NameComponent } from './edit/name/name.component';
import { RegistryIdComponent } from './edit/registry-id/registry-id.component';
import { SiteNameComponent } from './edit/site-name/site-name.component';
import { SopIdComponent } from './edit/sop-id/sop-id.component';
import { TriggerAirComponent } from './trigger-air/trigger-air.component';
import { TriggerDoalComponent } from './trigger-doal/trigger-doal.component';
import { AerReinitializeComponent } from './workflows/aer-reinitialize/aer-reinitialize.component';

@NgModule({
  declarations: [
    AccountComponent,
    AddressComponent,
    AerReinitializeComponent,
    DetailsComponent,
    FaStatusComponent,
    NameComponent,
    NoteFileDownloadComponent,
    RegistryIdComponent,
    SiteNameComponent,
    SopIdComponent,
    TriggerAirComponent,
    TriggerDoalComponent,
  ],
  imports: [AccountsRoutingModule, SharedModule, SharedUserModule],
  providers: [ItemLinkPipe, legalEntityFormFactory],
})
export class AccountsModule {}
