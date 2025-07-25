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
import { EmissionsCalculationDTO } from '../model/emissionsCalculationDTO';
import { EmissionsCalculationParamsDTO } from '../model/emissionsCalculationParamsDTO';
import { MeasurementEmissionsCalculationDTO } from '../model/measurementEmissionsCalculationDTO';
import { MeasurementEmissionsCalculationParamsDTO } from '../model/measurementEmissionsCalculationParamsDTO';
import { PfcEmissionsCalculationDTO } from '../model/pfcEmissionsCalculationDTO';
import { PfcEmissionsCalculationParamsDTO } from '../model/pfcEmissionsCalculationParamsDTO';
import { BASE_PATH } from '../variables';

@Injectable({
  providedIn: 'root',
})
export class ReportingService {
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
   * Calculates emissions using AER calculation approach
   * @param emissionsCalculationParamsDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public calculateEmissions(
    emissionsCalculationParamsDTO: EmissionsCalculationParamsDTO,
  ): Observable<EmissionsCalculationDTO>;
  public calculateEmissions(
    emissionsCalculationParamsDTO: EmissionsCalculationParamsDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<EmissionsCalculationDTO>>;
  public calculateEmissions(
    emissionsCalculationParamsDTO: EmissionsCalculationParamsDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<EmissionsCalculationDTO>>;
  public calculateEmissions(
    emissionsCalculationParamsDTO: EmissionsCalculationParamsDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<EmissionsCalculationDTO>;
  public calculateEmissions(
    emissionsCalculationParamsDTO: EmissionsCalculationParamsDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (emissionsCalculationParamsDTO === null || emissionsCalculationParamsDTO === undefined) {
      throw new Error(
        'Required parameter emissionsCalculationParamsDTO was null or undefined when calling calculateEmissions.',
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

    return this.httpClient.post<EmissionsCalculationDTO>(
      `${this.configuration.basePath}/v1.0/reporting/calculation/calculate-emissions`,
      emissionsCalculationParamsDTO,
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
   * Calculates emissions using AER calculation approach
   * @param measurementEmissionsCalculationParamsDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public calculateMeasurementCO2Emissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
  ): Observable<MeasurementEmissionsCalculationDTO>;
  public calculateMeasurementCO2Emissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<MeasurementEmissionsCalculationDTO>>;
  public calculateMeasurementCO2Emissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<MeasurementEmissionsCalculationDTO>>;
  public calculateMeasurementCO2Emissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<MeasurementEmissionsCalculationDTO>;
  public calculateMeasurementCO2Emissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (measurementEmissionsCalculationParamsDTO === null || measurementEmissionsCalculationParamsDTO === undefined) {
      throw new Error(
        'Required parameter measurementEmissionsCalculationParamsDTO was null or undefined when calling calculateMeasurementCO2Emissions.',
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

    return this.httpClient.post<MeasurementEmissionsCalculationDTO>(
      `${this.configuration.basePath}/v1.0/reporting/measurement/co2/calculate-emissions`,
      measurementEmissionsCalculationParamsDTO,
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
   * Calculates emissions using AER calculation approach
   * @param measurementEmissionsCalculationParamsDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public calculateMeasurementN2OEmissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
  ): Observable<MeasurementEmissionsCalculationDTO>;
  public calculateMeasurementN2OEmissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<MeasurementEmissionsCalculationDTO>>;
  public calculateMeasurementN2OEmissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<MeasurementEmissionsCalculationDTO>>;
  public calculateMeasurementN2OEmissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<MeasurementEmissionsCalculationDTO>;
  public calculateMeasurementN2OEmissions(
    measurementEmissionsCalculationParamsDTO: MeasurementEmissionsCalculationParamsDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (measurementEmissionsCalculationParamsDTO === null || measurementEmissionsCalculationParamsDTO === undefined) {
      throw new Error(
        'Required parameter measurementEmissionsCalculationParamsDTO was null or undefined when calling calculateMeasurementN2OEmissions.',
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

    return this.httpClient.post<MeasurementEmissionsCalculationDTO>(
      `${this.configuration.basePath}/v1.0/reporting/measurement/n2o/calculate-emissions`,
      measurementEmissionsCalculationParamsDTO,
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
   * Calculates emissions using AER calculation approach
   * @param pfcEmissionsCalculationParamsDTO
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public calculatePfcEmissions(
    pfcEmissionsCalculationParamsDTO: PfcEmissionsCalculationParamsDTO,
  ): Observable<PfcEmissionsCalculationDTO>;
  public calculatePfcEmissions(
    pfcEmissionsCalculationParamsDTO: PfcEmissionsCalculationParamsDTO,
    observe: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpResponse<PfcEmissionsCalculationDTO>>;
  public calculatePfcEmissions(
    pfcEmissionsCalculationParamsDTO: PfcEmissionsCalculationParamsDTO,
    observe: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<HttpEvent<PfcEmissionsCalculationDTO>>;
  public calculatePfcEmissions(
    pfcEmissionsCalculationParamsDTO: PfcEmissionsCalculationParamsDTO,
    observe: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<PfcEmissionsCalculationDTO>;
  public calculatePfcEmissions(
    pfcEmissionsCalculationParamsDTO: PfcEmissionsCalculationParamsDTO,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: 'application/json' },
  ): Observable<any> {
    if (pfcEmissionsCalculationParamsDTO === null || pfcEmissionsCalculationParamsDTO === undefined) {
      throw new Error(
        'Required parameter pfcEmissionsCalculationParamsDTO was null or undefined when calling calculatePfcEmissions.',
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

    return this.httpClient.post<PfcEmissionsCalculationDTO>(
      `${this.configuration.basePath}/v1.0/reporting/calculation/pfc/calculate-emissions`,
      pfcEmissionsCalculationParamsDTO,
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
