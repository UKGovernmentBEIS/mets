import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  BusinessError,
} from '../../error/business-error/business-error';

const verificationBodyBusinessLink: Pick<BusinessError, 'link' | 'linkText'> = {
  linkText: 'Return to manage verification bodies',
  link: ['/verification-bodies'],
};

export const savePartiallyNotFoundVerificationBodyError =
  buildSavePartiallyNotFoundError().withLink(verificationBodyBusinessLink);

export const saveNotFoundVerificationBodyError = buildSaveNotFoundError().withLink(verificationBodyBusinessLink);

export const viewNotFoundVerificationBodyError = buildViewNotFoundError().withLink(verificationBodyBusinessLink);

export const disabledVerificationBodyError = new BusinessError(
  'This action cannot be performed because the verification body has been disabled',
).withLink(verificationBodyBusinessLink);
