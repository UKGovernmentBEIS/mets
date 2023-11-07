import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  BusinessError,
} from '../../error/business-error/business-error';

const regulatorBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to regulator users page',
  fragment: 'regulator-users',
};

export const saveNotFoundRegulatorError = buildSaveNotFoundError().withLink(regulatorBusinessLink);

export const savePartiallyNotFoundRegulatorError = buildSavePartiallyNotFoundError().withLink(regulatorBusinessLink);

export const viewNotFoundRegulatorError = buildViewNotFoundError().withLink(regulatorBusinessLink);

const siteContactBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to site contacts page',
  fragment: 'site-contacts',
};

export const savePartiallyNotFoundSiteContactError =
  buildSavePartiallyNotFoundError().withLink(siteContactBusinessLink);

const externalContactBusinessLink: Pick<BusinessError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to external contacts page',
  fragment: 'external-contacts',
};

export const viewNotFoundExternalContactError = buildViewNotFoundError().withLink(externalContactBusinessLink);

export const saveNotFoundExternalContactError = buildSaveNotFoundError().withLink(externalContactBusinessLink);
