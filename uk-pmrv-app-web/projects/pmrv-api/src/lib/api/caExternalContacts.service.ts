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
import { CaExternalContactDTO } from '../model/caExternalContactDTO';
import { CaExternalContactRegistrationDTO } from '../model/caExternalContactRegistrationDTO';
import { CaExternalContactsDTO } from '../model/caExternalContactsDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class CaExternalContactsService {
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
   * Creates a new ca external contact
   * @param caExternalContactRegistrationDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public createCaExternalContact(caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO): Observable<any>;
  public createCaExternalContact(
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<any>>;
  public createCaExternalContact(
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<any>>;
  public createCaExternalContact(
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any>;
  public createCaExternalContact(
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (caExternalContactRegistrationDTO === null || caExternalContactRegistrationDTO === undefined) {
      throw new Error(
        'Required parameter caExternalContactRegistrationDTO was null or undefined when calling createCaExternalContact.',
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
    const consumes: string[] = ['application/json'];
    const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
    if (httpContentTypeSelected !== undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.post<any>(
      `${this.configuration.basePath}/v1.0/ca-external-contacts`,
      caExternalContactRegistrationDTO,
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
   * Deletes the ca external contact with specified id
   * @param id The ca external contact id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public deleteCaExternalContactById(id: number): Observable<any>;
  public deleteCaExternalContactById(
    id: number,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<any>>;
  public deleteCaExternalContactById(
    id: number,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<any>>;
  public deleteCaExternalContactById(
    id: number,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any>;
  public deleteCaExternalContactById(
    id: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling deleteCaExternalContactById.');
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

    return this.httpClient.delete<any>(
      `${this.configuration.basePath}/v1.0/ca-external-contacts/${encodeURIComponent(String(id))}`,
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
   * Edits the ca external contact with specified id
   * @param id The ca external contact id
   * @param caExternalContactRegistrationDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public editCaExternalContact(
    id: number,
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
  ): Observable<any>;
  public editCaExternalContact(
    id: number,
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<any>>;
  public editCaExternalContact(
    id: number,
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<any>>;
  public editCaExternalContact(
    id: number,
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any>;
  public editCaExternalContact(
    id: number,
    caExternalContactRegistrationDTO: CaExternalContactRegistrationDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling editCaExternalContact.');
    }
    if (caExternalContactRegistrationDTO === null || caExternalContactRegistrationDTO === undefined) {
      throw new Error(
        'Required parameter caExternalContactRegistrationDTO was null or undefined when calling editCaExternalContact.',
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
    const consumes: string[] = ['application/json'];
    const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
    if (httpContentTypeSelected !== undefined) {
      headers = headers.set('Content-Type', httpContentTypeSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.patch<any>(
      `${this.configuration.basePath}/v1.0/ca-external-contacts/${encodeURIComponent(String(id))}`,
      caExternalContactRegistrationDTO,
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
   * Returns the ca external contact with specified id
   * @param id The ca external contact id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCaExternalContactById(id: number): Observable<CaExternalContactDTO>;
  public getCaExternalContactById(
    id: number,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' | 'application/json' },
  ): Observable<HttpResponse<CaExternalContactDTO>>;
  public getCaExternalContactById(
    id: number,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' | 'application/json' },
  ): Observable<HttpEvent<CaExternalContactDTO>>;
  public getCaExternalContactById(
    id: number,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' | 'application/json' },
  ): Observable<CaExternalContactDTO>;
  public getCaExternalContactById(
    id: number,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' | 'application/json' },
  ): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getCaExternalContactById.');
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
      const httpHeaderAccepts: string[] = ['*/*', 'application/json'];
      httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType_: 'text' | 'json' = 'json';
    if (httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
      responseType_ = 'text';
    }

    return this.httpClient.get<CaExternalContactDTO>(
      `${this.configuration.basePath}/v1.0/ca-external-contacts/${encodeURIComponent(String(id))}`,
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
   * Retrieves the current regulator external contacts
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCaExternalContacts(): Observable<CaExternalContactsDTO>;
  public getCaExternalContacts(
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<CaExternalContactsDTO>>;
  public getCaExternalContacts(
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<CaExternalContactsDTO>>;
  public getCaExternalContacts(
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<CaExternalContactsDTO>;
  public getCaExternalContacts(
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
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

    return this.httpClient.get<CaExternalContactsDTO>(`${this.configuration.basePath}/v1.0/ca-external-contacts`, {
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: headers,
      observe: observe,
      reportProgress: reportProgress,
    });
  }
}
