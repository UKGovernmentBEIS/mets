import { NgModule } from '@angular/core';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BdrS2RoutingModule } from './bdrs2-routing.module';
import { BdrS2Service } from './core';

@NgModule({
  imports: [BdrS2RoutingModule, SharedModule, TaskSharedModule],
  providers: [BdrS2Service, CapitalizeFirstPipe, ItemNamePipe],
})
export class BdrS2Module {}
