import { Injectable } from '@angular/core';

import { Store } from '@core/store/store';

import { initialState, MiReportsState } from './mi-reports.state';

@Injectable()
export class MiReportsStore extends Store<MiReportsState> {
  constructor() {
    super(initialState);
  }

  setSearchTerm(searchTerm: string) {
    // reset to the first page so results are not shown against a stale page number
    this.setState({ ...this.getState(), searchTerm, page: 1 });
  }

  setSelectedCategory(selectedCategory: number | '') {
    // reset to the first page so results are not shown against a stale page number
    this.setState({ ...this.getState(), selectedCategory, page: 1 });
  }

  setShowFavouritesOnly(showFavouritesOnly: boolean) {
    // reset to the first page so results are not shown against a stale page number
    this.setState({ ...this.getState(), showFavouritesOnly, page: 1 });
  }

  setPage(page: number) {
    this.setState({ ...this.getState(), page });
  }

  setTotal(total: number) {
    this.setState({ ...this.getState(), total });
  }
}
