import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { addDays, endOfDay, format, isAfter, isBefore, isEqual, startOfDay } from 'date-fns';

const isSameOrBefore = (date1, date2) => isBefore(date1, date2) || isEqual(date1, date2);

const isSameOrAfter = (date1, date2) => isEqual(date1, date2) || isAfter(date1, date2);

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

  const effectiveDateMax = format(endOfDay(addDays(new Date(), 28)), 'yyyy-MM-dd');

  return (
    (effectiveDate && feeDate
      ? isAfter(new Date(feeDate), new Date(effectiveDate)) &&
        isSameOrBefore(new Date(effectiveDateMax), new Date(effectiveDate))
      : effectiveDate
        ? isSameOrBefore(new Date(effectiveDateMax), new Date(effectiveDate))
        : true) &&
    isStoppedDateValid(stoppedDate) &&
    isAnnualEmissionsReportDateValid(annualEmissionsReportDate) &&
    isSurrenderDateValid(surrenderDate)
  );
};

const isStoppedDateValid = (stoppedDate: string) => {
  const todayMax = endOfDay(new Date());
  return stoppedDate ? isSameOrBefore(new Date(stoppedDate), todayMax) : true;
};
const isAnnualEmissionsReportDateValid = (annualEmissionsReportDate: string) => {
  const todayMin = startOfDay(new Date());

  return annualEmissionsReportDate ? isSameOrAfter(new Date(annualEmissionsReportDate), todayMin) : true;
};

const isSurrenderDateValid = (surrenderDate) => {
  const todayMin = startOfDay(new Date());
  return surrenderDate ? isSameOrAfter(new Date(surrenderDate), todayMin) : true;
};
