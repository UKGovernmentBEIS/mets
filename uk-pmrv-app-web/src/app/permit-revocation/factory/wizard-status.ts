import { PermitRevocation } from 'pmrv-api';

export const stepStatus = (step: number, permitRevocation: PermitRevocation): boolean => {
  switch (step) {
    case 0:
      return true;
    case 1:
      return step1(permitRevocation);
    case 2:
      return step2(permitRevocation);
    case 3:
      return step3(permitRevocation);
    case 4:
      return step4(permitRevocation);
    case 5:
      return step5(permitRevocation);
    case 6:
      return step6(permitRevocation);
    case 7:
      return step7(permitRevocation);
  }
};

export const completed = (permitRevocation: PermitRevocation, isFinalAlrVisible: boolean): boolean => {
  return (
    step1(permitRevocation) &&
    step2(permitRevocation) &&
    step3(permitRevocation) &&
    step4(permitRevocation) &&
    (isFinalAlrVisible ? step5(permitRevocation) : true) &&
    step6(permitRevocation) &&
    step7(permitRevocation)
  );
};

const step1 = (permitRevocation: PermitRevocation): boolean => !!permitRevocation?.reason;
const step2 = (permitRevocation: PermitRevocation): boolean =>
  permitRevocation?.activitiesStopped === false || permitRevocation?.stoppedDate != undefined;
const step3 = (permitRevocation: PermitRevocation): boolean => !!permitRevocation?.effectiveDate;
const step4 = (permitRevocation: PermitRevocation): boolean =>
  permitRevocation?.annualEmissionsReportRequired === false || permitRevocation?.annualEmissionsReportDate != undefined;
const step5 = (permitRevocation: PermitRevocation): boolean => {
  return permitRevocation?.alrRequired === false || permitRevocation?.alrReportDate != undefined;
};

const step6 = (permitRevocation: PermitRevocation): boolean =>
  permitRevocation?.surrenderRequired === false || permitRevocation?.surrenderDate != undefined;
const step7 = (permitRevocation: PermitRevocation): boolean =>
  permitRevocation?.feeCharged === false || permitRevocation?.feeDate != undefined;
