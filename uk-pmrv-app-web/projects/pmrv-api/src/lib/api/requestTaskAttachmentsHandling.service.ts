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
import { HttpClient, HttpEvent, HttpHeaders, HttpParameterCodec, HttpParams, HttpResponse } from '@angular/common/http';
import { Inject, Injectable, Optional } from '@angular/core';

import { Observable } from 'rxjs';

import { Configuration } from '../configuration';
import { CustomHttpParameterCodec } from '../encoder';
import { FileToken } from '../model/fileToken';
import { FileUuidDTO } from '../model/fileUuidDTO';
import { RequestTaskAttachmentActionProcessDTO } from '../model/requestTaskAttachmentActionProcessDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class RequestTaskAttachmentsHandlingService {
  protected basePath = 'http://localhost:8080/api';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();
  public encoder: HttpParameterCodec;

  constructor(
    protected httpClient: HttpClient,
    @Optional() @Inject(BASE_PATH) basePath: string,
    @Optional() configuration: Configuration,
  ) {
    if (configuration) {
      this.configuration = configuration;
    }
    if (typeof this.configuration.basePath !== 'string') {
      if (typeof basePath !== 'string') {
        basePath = this.basePath;
      }
      this.configuration.basePath = basePath;
    }
    this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
  }

  /**
   * @param consumes string[] mime-types
   * @return true: consumes contains 'multipart/form-data', false: otherwise
   */
  private canConsumeForm(consumes: string[]): boolean {
    const form = 'multipart/form-data';
    for (const consume of consumes) {
      if (form === consume) {
        return true;
      }
    }
    return false;
  }

  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === 'object' && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === 'object') {
      if (Array.isArray(value)) {
        (value as any[]).forEach((elem) => (httpParams = this.addToHttpParamsRecursive(httpParams, elem, key)));
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key, (value as Date).toISOString().substr(0, 10));
        } else {
          throw Error('key may not be null if value is Date');
        }
      } else {
        Object.keys(value).forEach(
          (k) => (httpParams = this.addToHttpParamsRecursive(httpParams, value[k], key != null ? `${key}.${k}` : k)),
        );
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error('key may not be null if value is not object or array');
    }
    return httpParams;
  }

  /**
   * Generate the token to get the file with the provided uuid that belongs to the provided task
   * @param id The request task id
   * @param attachmentUuid The attachment uuid
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public generateRequestTaskGetFileAttachmentToken(id: number, attachmentUuid: string): Observable<FileToken>;
  public generateRequestTaskGetFileAttachmentToken(
    id: number,
    attachmentUuid: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<FileToken>>;
  public generateRequestTaskGetFileAttachmentToken(
    id: number,
    attachmentUuid: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<FileToken>>;
  public generateRequestTaskGetFileAttachmentToken(
    id: number,
    attachmentUuid: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<FileToken>;
  public generateRequestTaskGetFileAttachmentToken(
    id: number,
    attachmentUuid: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error(
        'Required parameter id was null or undefined when calling generateRequestTaskGetFileAttachmentToken.',
      );
    }
    if (attachmentUuid === null || attachmentUuid === undefined) {
      throw new Error(
        'Required parameter attachmentUuid was null or undefined when calling generateRequestTaskGetFileAttachmentToken.',
      );
    }

    let queryParameters = new HttpParams({ encoder: this.encoder });
    if (attachmentUuid !== undefined && attachmentUuid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, <any>attachmentUuid, 'attachmentUuid');
    }

    let headers = this.defaultHeaders;

    // authentication (bearerAuth) required
    const credential = this.configuration.lookupCredential('bearerAuth');
    if (credential) {
      headers = headers.set('Authorization', 'Bearer ' + credential);
    }

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['application/json'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<FileToken>(
      `${this.configuration.basePath}/v1.0/task-attachments/${encodeURIComponent(String(id))}`,
      {
        params: queryParameters,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Upload a request task attachment
   * @param requestTaskActionDetails
   * @param attachment The request task source file attachment
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public uploadRequestTaskAttachment(
    requestTaskActionDetails: RequestTaskAttachmentActionProcessDTO,
    attachment: Blob,
  ): Observable<FileUuidDTO>;
  public uploadRequestTaskAttachment(
    requestTaskActionDetails: RequestTaskAttachmentActionProcessDTO,
    attachment: Blob,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<FileUuidDTO>>;
  public uploadRequestTaskAttachment(
    requestTaskActionDetails: RequestTaskAttachmentActionProcessDTO,
    attachment: Blob,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<FileUuidDTO>>;
  public uploadRequestTaskAttachment(
    requestTaskActionDetails: RequestTaskAttachmentActionProcessDTO,
    attachment: Blob,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<FileUuidDTO>;
  public uploadRequestTaskAttachment(
    requestTaskActionDetails: RequestTaskAttachmentActionProcessDTO,
    attachment: Blob,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (requestTaskActionDetails === null || requestTaskActionDetails === undefined) {
      throw new Error(
        'Required parameter requestTaskActionDetails was null or undefined when calling uploadRequestTaskAttachment.',
      );
    }
    if (attachment === null || attachment === undefined) {
      throw new Error('Required parameter attachment was null or undefined when calling uploadRequestTaskAttachment.');
    }

    let headers = this.defaultHeaders;

    // authentication (bearerAuth) required
    const credential = this.configuration.lookupCredential('bearerAuth');
    if (credential) {
      headers = headers.set('Authorization', 'Bearer ' + credential);
    }

    let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['application/json'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    // to determine the Content-Type header
    const consumes: string[] = ['multipart/form-data'];

    const canConsumeForm = this.canConsumeForm(consumes);

    let formParams: { append(param: string, value: any): any };
    let useForm = false;
    const convertFormParamsToString = false;
    // use FormData to transmit files using content-type "multipart/form-data"
    // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
    useForm = canConsumeForm;
    if (useForm) {
      formParams = new FormData();
    } else {
      formParams = new HttpParams({ encoder: this.encoder });
    }

    if (requestTaskActionDetails !== undefined) {
      formParams =
        (formParams.append(
          'requestTaskActionDetails',
          useForm
            ? new Blob([JSON.stringify(requestTaskActionDetails)], { type: 'application/json' })
            : <any>requestTaskActionDetails,
        ) as any) || formParams;
    }
    if (attachment !== undefined) {
      formParams = (formParams.append('attachment', <any>attachment) as any) || formParams;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<FileUuidDTO>(
      `${this.configuration.basePath}/v1.0/task-attachments/upload`,
      convertFormParamsToString ? formParams.toString() : formParams,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }
}
