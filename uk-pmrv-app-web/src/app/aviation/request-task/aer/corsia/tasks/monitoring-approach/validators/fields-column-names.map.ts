import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

export const mapFieldsToColumnNames = (fields: [keyof AviationAerCorsiaAircraftTypeDetails]) => {
  const fieldColumnMap: { [key in keyof AviationAerCorsiaAircraftTypeDetails]: string } = {
    designator: 'A',
    subtype: 'B',
    fuelBurnRatio: 'C',
  };
  return fields.map((field) => fieldColumnMap[field]);
};
