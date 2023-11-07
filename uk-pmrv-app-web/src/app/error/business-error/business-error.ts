export class BusinessError {
  link: any[];
  linkText: string;
  fragment?: string;

  constructor(readonly heading: string) {}

  withLink?({ link, linkText, fragment }: Pick<BusinessError, 'link' | 'linkText' | 'fragment'>): this {
    this.link = link;
    this.linkText = linkText;
    this.fragment = fragment;

    return this;
  }
}

export const dashboardLink: Pick<BusinessError, 'link' | 'linkText'> = {
  linkText: 'Return to Dashboard',
  link: ['/dashboard'],
};

export const buildViewNotFoundError = () =>
  new BusinessError('This item cannot be viewed because the information no longer exists');

export const buildSaveNotFoundError = () =>
  new BusinessError('These changes cannot be saved because the information no longer exists');

export const buildViewPartiallyNotFoundError = () =>
  new BusinessError('This item cannot be viewed because some of the information no longer exists');

export const buildSavePartiallyNotFoundError = () =>
  new BusinessError('These changes cannot be saved because some of the information no longer exists');
