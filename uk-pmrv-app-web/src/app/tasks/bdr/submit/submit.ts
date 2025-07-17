import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { RequestTaskDTO } from 'pmrv-api';

const sameTitleAsItemNameTypes: Array<RequestTaskDTO['type']> = [
  'BDR_WAIT_FOR_VERIFICATION',
  'BDR_AMEND_WAIT_FOR_VERIFICATION',
  'BDR_WAIT_FOR_REGULATOR_REVIEW',
];

export const waitTasks: Array<RequestTaskDTO['type']> = [
  'BDR_WAIT_FOR_VERIFICATION',
  'BDR_AMEND_WAIT_FOR_VERIFICATION',
  'BDR_WAIT_FOR_REGULATOR_REVIEW',
];

export const bdrExpectedTaskTypes: Array<RequestTaskDTO['type']> = [
  'BDR_APPLICATION_SUBMIT',
  'BDR_APPLICATION_AMENDS_SUBMIT',
  'BDR_WAIT_FOR_VERIFICATION',
  'BDR_AMEND_WAIT_FOR_VERIFICATION',
  'BDR_WAIT_FOR_REGULATOR_REVIEW',
];

export const submitTitle = (requestTaskType: RequestTaskDTO['type'], year: number) => {
  if (sameTitleAsItemNameTypes.includes(requestTaskType)) {
    const itemNamePipe = new ItemNamePipe();

    return itemNamePipe.transform(requestTaskType, year);
  } else {
    switch (requestTaskType) {
      case 'BDR_APPLICATION_SUBMIT':
        return `Complete ${year} baseline data report`;
      case 'BDR_APPLICATION_AMENDS_SUBMIT':
        return `Amend ${year} baseline data report`;
    }
  }
};

export const warningText: Partial<Record<RequestTaskDTO['type'], string>> = {
  BDR_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement',
  BDR_AMEND_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement',
  BDR_WAIT_FOR_REGULATOR_REVIEW: 'Waiting for the regulator to complete the review',
};
