import { EmpCorsiaVariationModification } from '@aviation/shared/components/emp-corsia/variation-details-summary-template/util/variation-details';

export function getChangesFormData(data: any): any[] {
  const result = [];
  Object.entries(data).forEach(([key, values]) => {
    if (key === 'materialChanges' || key === 'nonMaterialChanges' || key === 'otherChanges') {
      (values as Array<EmpCorsiaVariationModification['type']>)?.forEach((value) => {
        result.push(value);
      });
    }
  });
  return result;
}
