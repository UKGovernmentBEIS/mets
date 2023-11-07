import { HttpErrorResponse } from '@angular/common/http';

import { BehaviorSubject, EMPTY } from 'rxjs';

export const templateError = (
  res: HttpErrorResponse,
  isErrorDisplayed$: BehaviorSubject<boolean>,
  errorMessage$: BehaviorSubject<string>,
) => {
  const templateName = res.error.data[0];
  const templateError = res.error.message;
  const errorMessageByCode = {
    NOTIF1000: `Sorry, there was a problem when evaluating the email template "${templateName}": ${templateError}`,
    NOTIF1001: `Sorry, there was a problem when evaluating the document template "${templateName}": ${templateError}`,
    NOTIF1002: `Sorry, there was a problem when evaluating the document template "${templateName}": ${templateError}`,
    NOTIF1003: `Sorry, there was a problem when evaluating the email template "${templateName}": ${templateError}`,
    ACCOUNT1001: 'The operator name must be changed before you can proceed. This one is already in use',
  };

  isErrorDisplayed$.next(true);
  errorMessage$.next(errorMessageByCode[res.error.code]);
  return EMPTY;
};
