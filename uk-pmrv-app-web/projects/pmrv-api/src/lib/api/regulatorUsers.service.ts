/**
 * PMRV API Documentation
 * PMRV API Documentation
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
import { RegulatorInvitedUserDTO } from '../model/regulatorInvitedUserDTO';
import { RegulatorUserDTO } from '../model/regulatorUserDTO';
import { RegulatorUserUpdateDTO } from '../model/regulatorUserUpdateDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class RegulatorUsersService {
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
   * Generate the token to get the signature of the user with the provided user id
   * @param userId The regulator user id the signature belongs to
   * @param signatureUuid The signature uuid
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public generateGetRegulatorSignatureToken(userId: string, signatureUuid: string): Observable<FileToken>;
  public generateGetRegulatorSignatureToken(
    userId: string,
    signatureUuid: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<FileToken>>;
  public generateGetRegulatorSignatureToken(
    userId: string,
    signatureUuid: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<FileToken>>;
  public generateGetRegulatorSignatureToken(
    userId: string,
    signatureUuid: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<FileToken>;
  public generateGetRegulatorSignatureToken(
    userId: string,
    signatureUuid: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error(
        'Required parameter userId was null or undefined when calling generateGetRegulatorSignatureToken.',
      );
    }
    if (signatureUuid === null || signatureUuid === undefined) {
      throw new Error(
        'Required parameter signatureUuid was null or undefined when calling generateGetRegulatorSignatureToken.',
      );
    }

    let queryParameters = new HttpParams({ encoder: this.encoder });
    if (signatureUuid !== undefined && signatureUuid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, <any>signatureUuid, 'signatureUuid');
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
      `${this.configuration.basePath}/v1.0/regulator-users/${encodeURIComponent(String(userId))}/signature`,
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
   * Retrieves the user of type REGULATOR that corresponds to the provided user id
   * @param userId The regulator user id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getRegulatorUserByCaAndId(userId: string): Observable<RegulatorUserDTO>;
  public getRegulatorUserByCaAndId(
    userId: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<RegulatorUserDTO>>;
  public getRegulatorUserByCaAndId(
    userId: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<RegulatorUserDTO>>;
  public getRegulatorUserByCaAndId(
    userId: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<RegulatorUserDTO>;
  public getRegulatorUserByCaAndId(
    userId: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error('Required parameter userId was null or undefined when calling getRegulatorUserByCaAndId.');
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

    return this.httpClient.get<RegulatorUserDTO>(
      `${this.configuration.basePath}/v1.0/regulator-users/${encodeURIComponent(String(userId))}`,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Invite new regulator user
   * @param regulatorInvitedUser
   * @param signature The signature file
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public inviteRegulatorUserToCA(regulatorInvitedUser: RegulatorInvitedUserDTO, signature?: Blob): Observable<any>;
  public inviteRegulatorUserToCA(
    regulatorInvitedUser: RegulatorInvitedUserDTO,
    signature: Blob,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<any>>;
  public inviteRegulatorUserToCA(
    regulatorInvitedUser: RegulatorInvitedUserDTO,
    signature: Blob,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<any>>;
  public inviteRegulatorUserToCA(
    regulatorInvitedUser: RegulatorInvitedUserDTO,
    signature: Blob,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any>;
  public inviteRegulatorUserToCA(
    regulatorInvitedUser: RegulatorInvitedUserDTO,
    signature?: Blob,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (regulatorInvitedUser === null || regulatorInvitedUser === undefined) {
      throw new Error(
        'Required parameter regulatorInvitedUser was null or undefined when calling inviteRegulatorUserToCA.',
      );
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

    if (regulatorInvitedUser !== undefined) {
      formParams =
        (formParams.append(
          'regulatorInvitedUser',
          useForm
            ? new Blob([JSON.stringify(regulatorInvitedUser)], { type: 'application/json' })
            : <any>regulatorInvitedUser,
        ) as any) || formParams;
    }
    if (signature !== undefined) {
      formParams = (formParams.append('signature', <any>signature) as any) || formParams;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<any>(
      `${this.configuration.basePath}/v1.0/regulator-users/invite`,
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

  /**
   * Resets the 2FA device for the user of type REGULATOR that corresponds to the provided user id
   * @param userId Regulator\&#39;s user id to reset 2FA
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public resetRegulator2Fa(userId: string): Observable<RegulatorUserUpdateDTO>;
  public resetRegulator2Fa(
    userId: string,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<RegulatorUserUpdateDTO>>;
  public resetRegulator2Fa(
    userId: string,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<RegulatorUserUpdateDTO>>;
  public resetRegulator2Fa(
    userId: string,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<RegulatorUserUpdateDTO>;
  public resetRegulator2Fa(
    userId: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error('Required parameter userId was null or undefined when calling resetRegulator2Fa.');
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

    return this.httpClient.post<RegulatorUserUpdateDTO>(
      `${this.configuration.basePath}/v1.0/regulator-users/${encodeURIComponent(String(userId))}/reset-2fa`,
      null,
      {
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Updates the current regulator user
   * @param regulatorUserUpdateDTO
   * @param signature The signature file
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public updateCurrentRegulatorUser(
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature?: Blob,
  ): Observable<RegulatorUserUpdateDTO>;
  public updateCurrentRegulatorUser(
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<RegulatorUserUpdateDTO>>;
  public updateCurrentRegulatorUser(
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<RegulatorUserUpdateDTO>>;
  public updateCurrentRegulatorUser(
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<RegulatorUserUpdateDTO>;
  public updateCurrentRegulatorUser(
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature?: Blob,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (regulatorUserUpdateDTO === null || regulatorUserUpdateDTO === undefined) {
      throw new Error(
        'Required parameter regulatorUserUpdateDTO was null or undefined when calling updateCurrentRegulatorUser.',
      );
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

    if (regulatorUserUpdateDTO !== undefined) {
      formParams =
        (formParams.append(
          'regulatorUserUpdateDTO',
          useForm
            ? new Blob([JSON.stringify(regulatorUserUpdateDTO)], { type: 'application/json' })
            : <any>regulatorUserUpdateDTO,
        ) as any) || formParams;
    }
    if (signature !== undefined) {
      formParams = (formParams.append('signature', <any>signature) as any) || formParams;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<RegulatorUserUpdateDTO>(
      `${this.configuration.basePath}/v1.0/regulator-users`,
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

  /**
   * Updates the user of type REGULATOR that corresponds to the provided user id
   * @param userId The regulator user id to update
   * @param regulatorUserUpdateDTO
   * @param signature The signature file
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public updateRegulatorUserByCaAndId(
    userId: string,
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature?: Blob,
  ): Observable<RegulatorUserUpdateDTO>;
  public updateRegulatorUserByCaAndId(
    userId: string,
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<RegulatorUserUpdateDTO>>;
  public updateRegulatorUserByCaAndId(
    userId: string,
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<RegulatorUserUpdateDTO>>;
  public updateRegulatorUserByCaAndId(
    userId: string,
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature: Blob,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<RegulatorUserUpdateDTO>;
  public updateRegulatorUserByCaAndId(
    userId: string,
    regulatorUserUpdateDTO: RegulatorUserUpdateDTO,
    signature?: Blob,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (userId === null || userId === undefined) {
      throw new Error('Required parameter userId was null or undefined when calling updateRegulatorUserByCaAndId.');
    }
    if (regulatorUserUpdateDTO === null || regulatorUserUpdateDTO === undefined) {
      throw new Error(
        'Required parameter regulatorUserUpdateDTO was null or undefined when calling updateRegulatorUserByCaAndId.',
      );
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

    if (regulatorUserUpdateDTO !== undefined) {
      formParams =
        (formParams.append(
          'regulatorUserUpdateDTO',
          useForm
            ? new Blob([JSON.stringify(regulatorUserUpdateDTO)], { type: 'application/json' })
            : <any>regulatorUserUpdateDTO,
        ) as any) || formParams;
    }
    if (signature !== undefined) {
      formParams = (formParams.append('signature', <any>signature) as any) || formParams;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<RegulatorUserUpdateDTO>(
      `${this.configuration.basePath}/v1.0/regulator-users/${encodeURIComponent(String(userId))}`,
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