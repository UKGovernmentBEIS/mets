import { map, OperatorFunction, pipe } from 'rxjs';

import { FeatureName, FeatureState } from './feature.state';

export const selectIsFeatureEnabled = (feature: FeatureName): OperatorFunction<FeatureState, boolean> =>
  pipe(map((state) => state.features[feature]));

export const selectMeasurementId: OperatorFunction<FeatureState, string> = pipe(map(state => state.analytics.measurementId));
export const selectPropertyId: OperatorFunction<FeatureState, string> = pipe(map(state => state.analytics.propertyId));
