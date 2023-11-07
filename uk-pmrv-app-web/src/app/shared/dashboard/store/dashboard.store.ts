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
}
