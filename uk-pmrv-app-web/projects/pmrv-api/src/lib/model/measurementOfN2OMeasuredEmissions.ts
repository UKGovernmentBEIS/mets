/**
 * PMRV API Documentation
 * PMRV API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { NoHighestRequiredTierJustification } from './noHighestRequiredTierJustification';

export interface MeasurementOfN2OMeasuredEmissions {
  measurementDevicesOrMethods: Array<string>;
  samplingFrequency: 'CONTINUOUS' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'BI_ANNUALLY' | 'ANNUALLY' | 'OTHER';
  otherSamplingFrequency?: string;
  isHighestRequiredTier?: boolean;
  noHighestRequiredTierJustification?: NoHighestRequiredTierJustification;
  tier: 'TIER_1' | 'TIER_2' | 'TIER_3';
}