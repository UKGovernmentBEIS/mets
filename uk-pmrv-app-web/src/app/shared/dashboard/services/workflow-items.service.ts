import { Inject, Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { ACCOUNT_TYPE } from '@core/providers';
import { AccountType } from '@core/store/auth';

import {
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
} from 'pmrv-api';

import { WorkflowItemsAssignmentType } from '../store';

@Injectable()
export class WorkflowItemsService {
  constructor(
    private readonly itemsAssignedToMeService: ItemsAssignedToMeService,
    private readonly itemsAssignedToOthersService: ItemsAssignedToOthersService,
    private readonly unassignedItemsService: UnassignedItemsService,
    @Inject(ACCOUNT_TYPE) private readonly accountType: AccountType,
  ) {}

  getItems(
    type: WorkflowItemsAssignmentType,
    page: number,
    pageSize: number,
    orderBy: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE',
    filterWorkflow?: string,
    accountSearchTerm?: string,
  ): Observable<ItemDTOResponse> {
    const serviceMethod = this.getServiceMethod(type);
    return serviceMethod(this.accountType, page - 1, pageSize, orderBy, filterWorkflow, accountSearchTerm);
  }

  private getServiceMethod(type: WorkflowItemsAssignmentType) {
    switch (type) {
      case 'unassigned':
        return this.unassignedItemsService.getUnassignedItems.bind(this.unassignedItemsService);
      case 'assigned-to-others':
        return this.itemsAssignedToOthersService.getAssignedToOthersItems.bind(this.itemsAssignedToOthersService);
      case 'assigned-to-me':
      default:
        return this.itemsAssignedToMeService.getAssignedItems.bind(this.itemsAssignedToMeService);
    }
  }
}
