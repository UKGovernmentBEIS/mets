import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import moment from 'moment';

export const resolveApplyStatus = (state: PermitRevocationState): TaskItemStatus => {
  const permitRevocation = state.permitRevocation;
  return !notInNeedsForReview(state)
    ? 'needs review'
    : state.sectionsCompleted?.REVOCATION_APPLY
    ? 'complete'
    : permitRevocation?.reason !== undefined
    ? 'in progress'
    : 'not started';
};

export const resolveWithDrawStatus = (state: PermitRevocationState): TaskItemStatus => {
  return state?.reason ? 'complete' : 'not started';
};

// Check if the effective date of the permit revocation notice is at least 28days after today
// Also checks if fee date is after effective date and returns a boolean
// and if stopped date, annual emissions report date, surrender date are valid
export const notInNeedsForReview = (state: PermitRevocationState): boolean => {
  const effectiveDate = state?.permitRevocation?.effectiveDate;
  const feeDate = state?.permitRevocation?.feeDate;
  const stoppedDate = state?.permitRevocation?.stoppedDate;
  const annualEmissionsReportDate = state?.permitRevocation?.annualEmissionsReportDate;
  const surrenderDate = state?.permitRevocation?.surrenderDate;
  const add28Days = moment().add(28, 'd').set({ hour: 23, minute: 59, second: 59, millisecond: 59 });
  add28Days.toISOString();

  const effectiveDateMax = moment(add28Days).format('YYYY-MM-DD');
  return (
    (effectiveDate && feeDate
      ? moment(feeDate).isAfter(effectiveDate) && moment(effectiveDateMax).isSameOrBefore(effectiveDate)
      : effectiveDate
      ? moment(effectiveDateMax).isSameOrBefore(effectiveDate)
      : true) &&
    isStoppedDateValid(stoppedDate) &&
    isAnnualEmissionsReportDateValid(annualEmissionsReportDate) &&
    isSurrenderDateValid(surrenderDate)
  );
};

const isStoppedDateValid = (stoppedDate: string) => {
  const todayMax = moment().set({ hour: 23, minute: 59, second: 59, millisecond: 59 });
  return stoppedDate ? moment(stoppedDate).isSameOrBefore(todayMax) : true;
};
const isAnnualEmissionsReportDateValid = (annualEmissionsReportDate: string) => {
  const todayMin = moment().set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
  return annualEmissionsReportDate ? moment(annualEmissionsReportDate).isSameOrAfter(todayMin) : true;
};
const isSurrenderDateValid = (surrenderDate: string) => {
  const todayMin = moment().set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
  return surrenderDate ? moment(surrenderDate).isSameOrAfter(todayMin) : true;
};
