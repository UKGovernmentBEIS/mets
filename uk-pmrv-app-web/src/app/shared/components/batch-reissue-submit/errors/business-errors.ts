import { BusinessError } from '../../../../error/business-error/business-error';

const baseUrl = '/workflows/batch-variations';

export const batchVariationsLink = function (): Pick<BusinessError, 'link' | 'linkText'> {
  return { linkText: 'Return to: batch variations', link: [baseUrl] };
};

export const anotherInProgressError = function (): BusinessError {
  return new BusinessError(
    'You cannot start a batch variation application as there is already one in progress.',
  ).withLink(batchVariationsLink());
};

export const noMatchingEmittersError = function (): BusinessError {
  return new BusinessError('There are no matching emitters.').withLink(batchVariationsLink());
};
