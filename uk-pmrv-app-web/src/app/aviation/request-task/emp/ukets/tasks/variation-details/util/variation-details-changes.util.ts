import { EmpUKEtsVariationModification } from '@aviation/shared/components/emp/variation-details-summary-template/util/variation-details';

export function getChangesFormData(data: any): any[] {
  const result = [];
  Object.entries(data).forEach(([key, values]) => {
    if (key === 'significantChanges' || key === 'nonSignificantChanges') {
      (values as Array<EmpUKEtsVariationModification['type']>)?.forEach((value) => {
        result.push(value);
      });
    }
  });
  return result;
}
