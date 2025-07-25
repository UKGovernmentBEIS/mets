/**
 * METS API Documentation
 * METS API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ActivityLevelChangeInformation } from './activityLevelChangeInformation';
import { DoalAdditionalDocuments } from './doalAdditionalDocuments';
import { DoalClosedDetermination } from './doalClosedDetermination';
import { DoalProceedToAuthorityDetermination } from './doalProceedToAuthorityDetermination';
import { OperatorActivityLevelReport } from './operatorActivityLevelReport';
import { VerificationReportOfTheActivityLevelReport } from './verificationReportOfTheActivityLevelReport';

export interface Doal {
  operatorActivityLevelReport: OperatorActivityLevelReport;
  verificationReportOfTheActivityLevelReport: VerificationReportOfTheActivityLevelReport;
  additionalDocuments: DoalAdditionalDocuments;
  activityLevelChangeInformation: ActivityLevelChangeInformation;
  determination: DoalClosedDetermination | DoalProceedToAuthorityDetermination;
}
