import { DataGapRequiredDetails, MethodologiesToCloseDataGaps } from 'pmrv-api';

export function dataGapsWizardComplete(dataGapsInfo: MethodologiesToCloseDataGaps): boolean {
  return (
    dataGapsInfo &&
    (dataGapsInfo?.dataGapRequired === false ||
      (dataGapsInfo?.dataGapRequired && isDataGapRequiredDetailsComplete(dataGapsInfo?.dataGapRequiredDetails)))
  );
}

function isDataGapRequiredDetailsComplete(dataGapRequiredDetails: DataGapRequiredDetails): boolean {
  return (
    !!dataGapRequiredDetails &&
    (dataGapRequiredDetails?.dataGapApproved ||
      (dataGapRequiredDetails?.dataGapApproved === false &&
        !!dataGapRequiredDetails?.dataGapApprovedDetails &&
        dataGapRequiredDetails?.dataGapApprovedDetails?.conservativeMethodUsed !== undefined &&
        dataGapRequiredDetails?.dataGapApprovedDetails?.materialMisstatement !== undefined))
  );
}
