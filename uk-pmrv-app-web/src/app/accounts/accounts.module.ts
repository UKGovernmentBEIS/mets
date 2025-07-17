import { NgModule, Type } from '@angular/core';

import { InspectionsComponent } from '@shared/accounts/components/inspections/inspections.component';
import { NoteFileDownloadComponent } from '@shared/components/note-file-download/note-file-download.component';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { SharedModule } from '@shared/shared.module';

import { legalEntityFormRegFactory } from '../installation-account-application/factories/legal-entity/legal-entity-form-reg.factory';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountComponent } from './account.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { AuditYearComponent } from './audit-year/audit-year.component';
import { DetailsComponent } from './details/details.component';
import { AddressComponent } from './edit/address/address.component';
import { FaStatusComponent } from './edit/fa-status/fa-status.component';
import { NameComponent } from './edit/name/name.component';
import { RegistryIdComponent } from './edit/registry-id/registry-id.component';
import { SiteNameComponent } from './edit/site-name/site-name.component';
import { SopIdComponent } from './edit/sop-id/sop-id.component';
import { TriggerAirComponent } from './trigger-air/trigger-air.component';
import { TriggerDoalComponent } from './trigger-doal/trigger-doal.component';
import { AerMarkAsNotRequiredComponent } from './workflows/aer-mark-as-not-required/aer-mark-as-not-required.component';
import { AerReinitializeComponent } from './workflows/aer-reinitialize/aer-reinitialize.component';

const standaloneComponents: Type<any>[] = [InspectionsComponent];

@NgModule({
  declarations: [
    AccountComponent,
    AddressComponent,
    AerMarkAsNotRequiredComponent,
    AerReinitializeComponent,
    AuditYearComponent,
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
  imports: [AccountsRoutingModule, SharedModule, SharedUserModule, ...standaloneComponents],
  providers: [ItemLinkPipe, legalEntityFormRegFactory],
})
export class AccountsModule {}
