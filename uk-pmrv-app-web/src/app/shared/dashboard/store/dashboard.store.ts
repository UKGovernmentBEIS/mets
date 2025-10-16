import { Injectable } from '@angular/core';

import { Store } from '@core/store/store';

import { ItemDTO } from 'pmrv-api';

import { DashboardState, initialState, WorkflowItemsAssignmentType } from './dashboard.state';

@Injectable()
export class DashboardStore extends Store<DashboardState> {
  constructor() {
    super(initialState);
  }

  setActiveTab(activeTab: WorkflowItemsAssignmentType) {
    this.setState({ ...this.getState(), activeTab });
  }

  setItems(items: ItemDTO[]) {
    this.setState({ ...this.getState(), items });
  }

  setTotal(total: number) {
    this.setState({ ...this.getState(), total });
  }

  setPage(page: number) {
    this.setState({ ...this.getState(), paging: { ...this.getState().paging, page } });
  }

  setOrderBy(order: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE') {
    this.setState({ ...this.getState(), order });
  }

  setFilterBy(filter: string) {
    this.setState({ ...this.getState(), filter });
  }

  setSearchBy(accountSearchTerm: string) {
    this.setState({ ...this.getState(), accountSearchTerm });
  }
}
