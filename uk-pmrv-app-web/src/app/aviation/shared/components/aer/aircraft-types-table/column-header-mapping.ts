import { GovukTableColumn } from 'govuk-components';

export const exampleData: any = [
  {
    aircraftTypeDesignator: 'Aircraft type designator',
    subType: 'Sub type (optional)',
    registrationNumber: 'Registration number',
    ownerOrLessor: 'Owner or lessor name',
    startDate: 'Start date (optional)',
    endDate: 'End date (optional)',
  },
];

export const defaultColumns: GovukTableColumn[] = [
  {
    header: 'Aircraft type designator',
    field: 'aircraftTypeDesignator',
  },
  {
    header: 'Sub type',
    field: 'subType',
  },
  {
    header: 'Registration number',
    field: 'registrationNumber',
  },
  {
    header: 'Owner or lessor',
    field: 'ownerOrLessor',
  },
  {
    header: 'Start date',
    field: 'startDate',
  },
  {
    header: 'End date',
    field: 'endDate',
  },
];

export const exampleColumns: GovukTableColumn[] = [
  {
    header: 'Column A',
    field: 'aircraftTypeDesignator',
  },
  {
    header: 'Column B',
    field: 'subType',
  },
  {
    header: 'Column C',
    field: 'registrationNumber',
  },
  {
    header: 'Column D',
    field: 'ownerOrLessor',
  },
  {
    header: 'Column E',
    field: 'startDate',
  },
  {
    header: 'Column F',
    field: 'endDate',
  },
];
