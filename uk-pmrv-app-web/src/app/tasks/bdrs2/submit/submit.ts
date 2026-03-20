import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { RequestTaskDTO } from 'pmrv-api';

const sameTitleAsItemNameTypes: Array<RequestTaskDTO['type']> = [
  'BDRS2_WAIT_FOR_VERIFICATION',
  'BDRS2_WAIT_FOR_REGULATOR_REVIEW',
  'BDRS2_AMEND_WAIT_FOR_VERIFICATION',
];

export const waitTasks: Array<RequestTaskDTO['type']> = [
  'BDRS2_WAIT_FOR_VERIFICATION',
  'BDRS2_WAIT_FOR_REGULATOR_REVIEW',
  'BDRS2_AMEND_WAIT_FOR_VERIFICATION',
];

export const bdrS2ExpectedTaskTypes: Array<RequestTaskDTO['type']> = [
  'BDRS2_APPLICATION_SUBMIT',
  'BDRS2_WAIT_FOR_VERIFICATION',
  'BDRS2_WAIT_FOR_REGULATOR_REVIEW',
  'BDRS2_APPLICATION_AMENDS_SUBMIT',
  'BDRS2_AMEND_WAIT_FOR_VERIFICATION',
];

export const submitTitle = (requestTaskType: RequestTaskDTO['type'], year: number) => {
  if (sameTitleAsItemNameTypes.includes(requestTaskType)) {
    const itemNamePipe = new ItemNamePipe();

    return itemNamePipe.transform(requestTaskType, year, String(year));
  } else {
    switch (requestTaskType) {
      case 'BDRS2_APPLICATION_SUBMIT':
        return `Complete ${year} stage 2 baseline data report`;
      case 'BDRS2_APPLICATION_AMENDS_SUBMIT':
        return `Amend ${year} stage 2 baseline data report`;
    }
  }
};

export const warningText: Partial<Record<RequestTaskDTO['type'], string>> = {
  BDRS2_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement',
  BDRS2_AMEND_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement',
  BDRS2_WAIT_FOR_REGULATOR_REVIEW: 'Waiting for the regulator to complete the review',
};
