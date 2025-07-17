export type FuelTypes = 'JET_KEROSENE' | 'JET_GASOLINE' | 'AVIATION_GASOLINE' | 'OTHER';
export type FuelTypesCorsia = 'JET_KEROSENE' | 'JET_GASOLINE' | 'AVIATION_GASOLINE' | 'TS_1' | 'NO_3_JET_FUEL';

type FuelType = {
  value: string;
  label: string;
  description: string;
  summaryDescription: string;
  consumption: string;
};

export const FUEL_TYPES: readonly FuelType[] = [
  {
    value: 'JET_KEROSENE',
    label: 'Jet kerosene',
    description: 'Jet kerosene (Jet A1 or Jet A)',
    summaryDescription: 'Jet kerosene (Jet A1 or Jet A)',
    consumption: '3.15 tCO2 per tonne of fuel',
  },
  {
    value: 'JET_GASOLINE',
    label: 'Jet gasoline',
    description: 'Jet gasoline (Jet B)',
    summaryDescription: 'Jet gasoline (Jet B)',
    consumption: '3.10 tCO2 per tonne of fuel',
  },
  {
    value: 'AVIATION_GASOLINE',
    label: 'Aviation gasoline',
    description: 'Aviation gasoline (AV gas)',
    summaryDescription: 'Aviation gasoline (AV gas)',
    consumption: '3.10 tCO2 per tonne of fuel',
  },
  {
    value: 'OTHER',
    label: 'Other',
    description:
      'Other (do not include a zero-rated sustainable aviation fuel that forms part of an emissions reduction claim)',
    summaryDescription: 'Other fuel (not including sustainable aviation fuel)',
    consumption: '',
  },
] as const;

export const FUEL_TYPES_CORSIA: readonly FuelType[] = [
  {
    value: 'JET_KEROSENE',
    label: 'Jet kerosene',
    description: 'Jet kerosene (Jet A1 or Jet A)',
    summaryDescription: 'Jet kerosene (Jet A1 or Jet A)',
    consumption: '3.16 tCO2 per tonne of fuel',
  },
  {
    value: 'JET_GASOLINE',
    label: 'Jet gasoline',
    description: 'Jet gasoline (Jet B)',
    summaryDescription: 'Jet gasoline (Jet B)',
    consumption: '3.10 tCO2 per tonne of fuel',
  },
  {
    value: 'AVIATION_GASOLINE',
    label: 'Aviation gasoline',
    description: 'Aviation gasoline (AV gas)',
    summaryDescription: 'Aviation gasoline (AV gas)',
    consumption: '3.10 tCO2 per tonne of fuel',
  },
  {
    value: 'TS_1',
    label: 'TS-1',
    description: 'TS-1',
    summaryDescription: 'TS-1',
    consumption: '3.16 tCO2 per tonne of fuel',
  },
  {
    value: 'NO_3_JET_FUEL',
    label: 'No.3 Jet Fuel',
    description: 'No.3 Jet Fuel',
    summaryDescription: 'No.3 Jet Fuel',
    consumption: '3.16 tCO2 per tonne of fuel',
  },
] as const;

export function mapFuelType(fuelType, isCorsia = false) {
  if (!fuelType) return '';

  const lowercaseFuelType = fuelType.toLowerCase();
  const fuelTypes = isCorsia ? FUEL_TYPES_CORSIA : FUEL_TYPES;

  const type = fuelTypes.find(
    (item: { summaryDescription: string; label: string }) =>
      item.summaryDescription.toLowerCase() === lowercaseFuelType || item.label.toLowerCase() === lowercaseFuelType,
  );

  return type ? type.value : '';
}

export function getFuelTypeValues(isCorsia = false): string[] {
  return isCorsia ? FUEL_TYPES_CORSIA.map((fuelType) => fuelType.value) : FUEL_TYPES.map((fuelType) => fuelType.value);
}

export function getSummaryDescription(fuelType: string, isCorsia = false): string {
  const fuelTypeObj = isCorsia
    ? FUEL_TYPES_CORSIA.find((ft) => ft.value === fuelType)
    : FUEL_TYPES.find((ft) => ft.value === fuelType);
  return fuelTypeObj ? fuelTypeObj.summaryDescription : fuelType;
}
