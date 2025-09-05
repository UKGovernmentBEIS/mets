import { ItemNamePipe } from '@shared/pipes/item-name.pipe';

import { RequestTaskDTO } from 'pmrv-api';

const sameTitleAsItemNameTypes: Array<RequestTaskDTO['type']> = ['HSE_TI_WAIT_FOR_REGULATOR_REVIEW'];

export const waitTasks: Array<RequestTaskDTO['type']> = ['HSE_TI_WAIT_FOR_REGULATOR_REVIEW'];

export const hsetiExpectedTaskTypes: Array<RequestTaskDTO['type']> = [
  'HSE_TI_APPLICATION_SUBMIT',
  'HSE_TI_APPLICATION_AMENDS_SUBMIT',
  'HSE_TI_WAIT_FOR_REGULATOR_REVIEW',
];

export const submitTitle = (requestTaskType: RequestTaskDTO['type'], allocationPeriod: string) => {
  if (sameTitleAsItemNameTypes.includes(requestTaskType)) {
    const itemNamePipe = new ItemNamePipe();

    return itemNamePipe.transform(requestTaskType, allocationPeriod);
  } else {
    switch (requestTaskType) {
      case 'HSE_TI_APPLICATION_SUBMIT':
        return `Complete ${allocationPeriod} HSE target increase application`;
      case 'HSE_TI_APPLICATION_AMENDS_SUBMIT':
        return `Amend ${allocationPeriod} HSE target increase application`;
    }
  }
};

export const warningText: Partial<Record<RequestTaskDTO['type'], string>> = {
  HSE_TI_WAIT_FOR_REGULATOR_REVIEW: 'Waiting for the regulator to complete the review',
};
