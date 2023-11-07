import { InPersonSiteVisit, NoSiteVisit, OpinionStatement, SiteVisit, VirtualSiteVisit } from 'pmrv-api';

export function opinionStatementWizardComplete(opinionStatement: OpinionStatement): boolean {
  return (
    opinionStatement &&
    monitoringApproachesComplete(opinionStatement) &&
    emissionFactorsComplete(opinionStatement) &&
    reviewEmissionsComplete(opinionStatement) &&
    additionalChangesComplete(opinionStatement) &&
    siteVisitsComplete(opinionStatement)
  );
}

function monitoringApproachesComplete(opinionStatement: OpinionStatement): boolean {
  return opinionStatement?.monitoringApproachDescription !== null;
}

function emissionFactorsComplete(opinionStatement: OpinionStatement): boolean {
  return opinionStatement?.emissionFactorsDescription !== null;
}

function reviewEmissionsComplete(opinionStatement: OpinionStatement): boolean {
  return (
    opinionStatement?.operatorEmissionsAcceptable === true ||
    (opinionStatement?.operatorEmissionsAcceptable === false &&
      opinionStatement?.monitoringApproachTypeEmissions !== null)
  );
}

function additionalChangesComplete(opinionStatement: OpinionStatement): boolean {
  return (
    opinionStatement?.additionalChangesNotCovered === false ||
    (opinionStatement?.additionalChangesNotCovered === true &&
      opinionStatement?.additionalChangesNotCoveredDetails !== null)
  );
}

function siteVisitsComplete(opinionStatement: OpinionStatement): boolean {
  return (
    opinionStatement?.siteVisit &&
    opinionStatement?.siteVisit?.siteVisitType &&
    siteVisitsPartialComplete(opinionStatement?.siteVisit)
  );
}

export function siteVisitsPartialComplete(siteVisit: SiteVisit): boolean {
  switch (siteVisit?.siteVisitType) {
    case 'IN_PERSON': {
      const inPersonVisit = siteVisit as InPersonSiteVisit;
      return (
        inPersonVisit?.teamMembers !== null &&
        inPersonVisit?.teamMembers !== undefined &&
        inPersonVisit?.visitDates?.length > 0
      );
    }
    case 'NO_VISIT': {
      const noVisit = siteVisit as NoSiteVisit;
      return noVisit?.reason !== null && noVisit?.reason !== undefined;
    }
    case 'VIRTUAL': {
      const virtualSiteVisit = siteVisit as VirtualSiteVisit;
      return (
        virtualSiteVisit?.teamMembers !== null &&
        virtualSiteVisit?.teamMembers !== undefined &&
        virtualSiteVisit?.reason !== null &&
        virtualSiteVisit?.reason !== undefined &&
        virtualSiteVisit?.visitDates?.length > 0
      );
    }
    default:
      return false;
  }
}
