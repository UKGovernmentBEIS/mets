export interface MiReportsState {
  searchTerm: string;
  selectedCategory: number | '';
  showFavouritesOnly: boolean;
  page: number;
  total: number;
}

export const initialState: MiReportsState = {
  searchTerm: '',
  selectedCategory: '',
  showFavouritesOnly: false,
  page: 1,
  total: 0,
};
