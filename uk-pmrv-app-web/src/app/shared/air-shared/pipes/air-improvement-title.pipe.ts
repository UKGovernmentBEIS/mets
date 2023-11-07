import { Pipe, PipeTransform } from '@angular/core';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';

import {
  AirImprovement,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementFallback,
  AirImprovementMeasurement,
} from 'pmrv-api';

@Pipe({
  name: 'airImprovementTitle',
})
export class AirImprovementTitlePipe implements PipeTransform {
  transform(airImprovement: AirImprovement, index: string): string {
    switch (airImprovement.type) {
      case 'CALCULATION_CO2':
        return this.transformAirImprovementCalculationCO2(airImprovement as AirImprovementCalculationCO2, index);
      case 'MEASUREMENT_CO2':
        return this.transformAirImprovementMeasurement(airImprovement as AirImprovementMeasurement, index);
      case 'FALLBACK':
        return this.transformAirImprovementFallback(airImprovement as AirImprovementFallback, index);
      case 'MEASUREMENT_N2O':
        return this.transformAirImprovementMeasurement(airImprovement as AirImprovementMeasurement, index);
      case 'CALCULATION_PFC':
        return this.transformAirImprovementCalculationPFC(airImprovement as AirImprovementCalculationPFC, index);
      default:
        return '';
    }
  }

  /**
   * Transform according to the following pattern
   * Item <No>: <Source stream>: <Source stream Description>:<Source stream Category>: <Parameter>
   * @param airImprovement
   * @param index
   * @private
   */
  private transformAirImprovementCalculationCO2(airImprovement: AirImprovementCalculationCO2, index: string): string {
    return `Item ${index}: ${airImprovement.sourceStreamReference}: ${this.transformType(
      airImprovement,
    )}: ${airImprovement.parameter.toLowerCase()}`;
  }

  /**
   * Transform according to the following pattern
   * Item <No>: <Source stream>: <Source stream Description>:<Source stream Category>: <Parameter>
   * @param airImprovement
   * @param index
   * @private
   */
  private transformAirImprovementCalculationPFC(airImprovement: AirImprovementCalculationPFC, index: string): string {
    return `Item ${index}: ${airImprovement.sourceStreamReference}: ${this.transformType(
      airImprovement,
    )}: ${airImprovement.parameter.toLowerCase()}`;
  }

  /**
   * Transform according to the following pattern
   * Item <No>: <Source stream>: <Source stream Description>: <Source stream Category>
   * @param airImprovement
   * @param index
   * @private
   */
  private transformAirImprovementFallback(airImprovement: AirImprovementFallback, index: string): string {
    return `Item ${index}: ${airImprovement.sourceStreamReference}: ${this.transformType(airImprovement)}`;
  }

  /**
   * Transform according to the following pattern
   * Item <No>: <Emission point>: <Emission point Description>: <Emission point Category>
   * @param airImprovement
   * @param index
   * @private
   */
  private transformAirImprovementMeasurement(airImprovement: AirImprovementMeasurement, index: string): string {
    return `Item ${index}: ${airImprovement.emissionPoint}: ${this.transformType(airImprovement)}`;
  }

  private transformType(airImprovement: AirImprovement) {
    const categoryTypeName = new CategoryTypeNamePipe();
    return categoryTypeName.transform(airImprovement?.categoryType).toLowerCase();
  }
}
