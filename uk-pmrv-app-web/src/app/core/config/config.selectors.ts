import { map, OperatorFunction, pipe } from 'rxjs';

import { ConfigState, FeatureName, FeaturesConfig } from '@core/config/config.state';

export const selectIsFeatureEnabled = (feature: FeatureName): OperatorFunction<ConfigState, boolean> =>
  pipe(map((state) => state.features[feature]));

export const selectMeasurementId: OperatorFunction<ConfigState, string> = pipe(
  map((state) => state.analytics.measurementId),
);
export const selectPropertyId: OperatorFunction<ConfigState, string> = pipe(map((state) => state.analytics.propertyId));

export const selectFeatures: OperatorFunction<ConfigState, FeaturesConfig> = pipe(map((state) => state.features));
