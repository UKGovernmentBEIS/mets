import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { RequestTaskDTO } from 'pmrv-api';

const sameTitleAsItemNameTypes: Array<RequestTaskDTO['type']> = [
  //   'BDR_WAIT_FOR_VERIFICATION',
  //   'BDR_AMEND_WAIT_FOR_VERIFICATION',
  //   'BDR_WAIT_FOR_REGULATOR_REVIEW',
];

export const bdrS2ExpectedTaskTypes: Array<RequestTaskDTO['type']> = [
  'BDRS2_APPLICATION_SUBMIT',
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
      case 'BDRS2_APPLICATION_SUBMIT':
        return `Complete ${year} stage 2 baseline data report`;
    }
  }
};
