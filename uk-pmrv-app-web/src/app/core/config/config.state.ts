export const FEATURES = [
  'aviation',
  'terms',
  'serviceGatewayEnabled',
  'inspectionsWfAccountsTabEnabled',
  'corsia3yearOffsettingEnabled',
  'co2-venting.permit-workflows.enabled',
  'bdrEnabled',
  'wastePermitEnabled',
] as const;
export type FeatureName = (typeof FEATURES)[number];
export type FeaturesConfig = { [key in FeatureName]?: boolean };

export interface ConfigState {
  features?: FeaturesConfig;
  analytics?: {
    measurementId: string;
    propertyId: string;
  };
  keycloakServerUrl?: string;
}

export const initialState: ConfigState = {
  features: {},
  analytics: {
    measurementId: '',
    propertyId: '',
  },
};
