import { BusinessError } from '../../../../error/business-error/business-error';

const baseUrl = '/workflows/batch-variations';

export const batchVariationsLink = function (isAviation: boolean): Pick<BusinessError, 'link' | 'linkText'> {
  return { linkText: 'Return to: batch variations', link: [`${isAviation ? '/aviation' : ''}${baseUrl}`] };
};

export const anotherInProgressError = function (isAviation: boolean): BusinessError {
  return new BusinessError(
    'You cannot start a batch variation application as there is already one in progress.',
  ).withLink(batchVariationsLink(isAviation));
};

export const noMatchingEmittersError = function (isAviation: boolean): BusinessError {
  return new BusinessError('There are no matching emitters.').withLink(batchVariationsLink(isAviation));
};
