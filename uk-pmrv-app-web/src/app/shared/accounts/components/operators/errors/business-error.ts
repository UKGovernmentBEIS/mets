import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  buildViewPartiallyNotFoundError,
  BusinessError,
} from '@error/business-error/business-error';

const buildOperatorBusinessLink: (accountId: number) => Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = (
  accountId,
) => ({
  link: ['/accounts', accountId],
  linkText: 'Return to the users, contacts and verifiers page',
  fragment: 'users',
});

const operatorErrorWithAccountIdFactory = (errorFactory: () => BusinessError) => (accountId: number) =>
  errorFactory().withLink(buildOperatorBusinessLink(accountId));

const buildNotAllowedAERVerificationInProgressError = () =>
  new BusinessError(
    'You cannot change verifier as there is an AER verification assessment in progress. You can either recall the assessment or wait until it is completed.',
  );

export const viewPartiallyNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildViewPartiallyNotFoundError);

export const savePartiallyNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildSavePartiallyNotFoundError);

export const saveNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildSaveNotFoundError);

export const viewNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildViewNotFoundError);

export const changeNotAllowedVerificationBodyError = operatorErrorWithAccountIdFactory(
  buildNotAllowedAERVerificationInProgressError,
);

export const saveNotFoundVerificationBodyError = (accountId: number) =>
  buildSaveNotFoundError().withLink({
    link: ['/accounts', accountId, 'verification-body', 'appoint'],
    linkText: 'Return to appoint a verifier page',
  });

export const appointedVerificationBodyError = (accountId: number) =>
  new BusinessError('A verification body is already appointed.').withLink({
    link: ['/accounts', accountId],
    linkText: 'Return to users, contacts and verifiers page',
    fragment: 'users',
  });

export const activeOperatorAdminError = operatorErrorWithAccountIdFactory(
  () => new BusinessError('You must have an active operator admin on your account'),
);

export const primaryContactError = operatorErrorWithAccountIdFactory(
  () => new BusinessError('You must have a primary contact on your account'),
);

export const financialContactError = operatorErrorWithAccountIdFactory(
  () => new BusinessError('You must have a financial contact on your account'),
);

export const serviceContactError = operatorErrorWithAccountIdFactory(
  () => new BusinessError('You must have a service contact on your account'),
);
