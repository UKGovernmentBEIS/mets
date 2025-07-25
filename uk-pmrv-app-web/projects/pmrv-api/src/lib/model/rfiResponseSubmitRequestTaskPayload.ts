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
import { RequestTaskPayload } from './requestTaskPayload';
import { RfiQuestionPayload } from './rfiQuestionPayload';
import { RfiResponsePayload } from './rfiResponsePayload';

export interface RfiResponseSubmitRequestTaskPayload extends RequestTaskPayload {
  rfiQuestionPayload: RfiQuestionPayload;
  rfiResponsePayload: RfiResponsePayload;
  rfiAttachments?: { [key: string]: string };
}
