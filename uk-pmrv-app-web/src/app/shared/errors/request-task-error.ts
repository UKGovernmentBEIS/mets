import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  BusinessError,
} from '@error/business-error/business-error';

export const taskNotFoundError = buildSaveNotFoundError().withLink({
  link: ['/dashboard'],
  linkText: 'Return to dashboard',
});

export const taskSubmitNotFoundError = buildSavePartiallyNotFoundError().withLink({
  link: ['/dashboard'],
  linkText: 'Return to dashboard',
});

export const requestTaskReassignedError = () =>
  new BusinessError('These changes cannot be saved because the task has been reassigned').withLink({
    link: ['/dashboard'],
    linkText: 'Return to dashboard',
  });
