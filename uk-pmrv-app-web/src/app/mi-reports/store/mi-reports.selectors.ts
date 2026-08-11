import { map, OperatorFunction, pipe } from 'rxjs';

import { MiReportsState } from './mi-reports.state';

export const selectSearchTerm: OperatorFunction<MiReportsState, string> = pipe(map((state) => state.searchTerm));
export const selectSelectedCategory: OperatorFunction<MiReportsState, number | ''> = pipe(
  map((state) => state.selectedCategory),
);
export const selectShowFavouritesOnly: OperatorFunction<MiReportsState, boolean> = pipe(
  map((state) => state.showFavouritesOnly),
);
export const selectPage: OperatorFunction<MiReportsState, number> = pipe(map((state) => state.page));
export const selectTotal: OperatorFunction<MiReportsState, number> = pipe(map((state) => state.total));
