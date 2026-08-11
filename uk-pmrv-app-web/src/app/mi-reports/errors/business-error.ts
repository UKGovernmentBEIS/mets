import { BusinessError } from '../../error/business-error/business-error';

const addCustomReportBusinessLink: Pick<BusinessError, 'link' | 'linkText'> = {
  linkText: 'Return to add a custom report',
  link: ['/mi-reports/add-custom-report'],
};

export const buildCustomReportError = (message: string) =>
  new BusinessError(message).withLink(addCustomReportBusinessLink);

const editCustomReportBusinessLink = (id: number): Pick<BusinessError, 'link' | 'linkText'> => ({
  linkText: 'Return to edit the report',
  link: ['/mi-reports/edit-custom-report', id],
});

export const buildEditCustomReportError = (message: string, id: number) =>
  new BusinessError(message).withLink(editCustomReportBusinessLink(id));

const viewCustomReportBusinessLink = (id: number): Pick<BusinessError, 'link' | 'linkText'> => ({
  linkText: 'Return to the report',
  link: ['/mi-reports/view-custom-report', id],
});

export const buildGenerateReportError = (message: string, id: number) =>
  new BusinessError(message).withLink(viewCustomReportBusinessLink(id));
