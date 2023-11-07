import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  BusinessError,
} from '../../error/business-error/business-error';

const verifierBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/verifiers'],
  fragment: 'verifier-users',
  linkText: 'Return to manage verifier users page',
};

export const savePartiallyNotFoundVerifierError = buildSavePartiallyNotFoundError().withLink(verifierBusinessLink);

export const saveNotFoundVerifierError = buildSaveNotFoundError().withLink(verifierBusinessLink);

export const viewNotFoundVerifierError = buildViewNotFoundError().withLink(verifierBusinessLink);

const siteContactsBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/verifiers'],
  fragment: 'site-contacts',
  linkText: 'Return to site contacts page',
};

export const savePartiallyNotFoundSiteContactsError =
  buildSavePartiallyNotFoundError().withLink(siteContactsBusinessLink);

export const deleteUniqueActiveVerifierError = new BusinessError(
  'You must have an active verifier admin on your account',
).withLink(verifierBusinessLink);
