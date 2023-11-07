import { Injectable } from '@angular/core';

import { Store } from '@core/store';

import { FeaturesConfig, FeatureState, initialState } from './feature.state';

@Injectable({ providedIn: 'root' })
export class FeatureStore extends Store<FeatureState> {
  constructor() {
    super(initialState);
  }

  setFeatures(features: FeaturesConfig) {
    this.setState({ ...this.getState(), features });
  }
}
