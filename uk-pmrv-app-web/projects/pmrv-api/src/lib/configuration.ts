import { HttpParameterCodec } from '@angular/common/http';

export interface ConfigurationParameters {
  /**
   *  @deprecated Since 5.0. Use credentials instead
   */
  apiKeys?: { [key: string]: string };
  username?: string;
  password?: string;
  /**
   *  @deprecated Since 5.0. Use credentials instead
   */
  accessToken?: string | (() => string);
  basePath?: string;
  withCredentials?: boolean;
  encoder?: HttpParameterCodec;
  /**
   * The keys are the names in the securitySchemes section of the OpenAPI
   * document. They should map to the value used for authentication
   * minus any standard prefixes such as 'Basic' or 'Bearer'.
   */
  credentials?: { [key: string]: string | (() => string | undefined) };
}

export class Configuration {
  /**
   *  @deprecated Since 5.0. Use credentials instead
   */
  apiKeys?: { [key: string]: string };
  username?: string;
  password?: string;
  /**
   *  @deprecated Since 5.0. Use credentials instead
   */
  accessToken?: string | (() => string);
  basePath?: string;
  withCredentials?: boolean;
  encoder?: HttpParameterCodec;
  /**
   * The keys are the names in the securitySchemes section of the OpenAPI
   * document. They should map to the value used for authentication
   * minus any standard prefixes such as 'Basic' or 'Bearer'.
   */
  credentials: { [key: string]: string | (() => string | undefined) };

  constructor(configurationParameters: ConfigurationParameters = {}) {
    this.apiKeys = configurationParameters.apiKeys;
    this.username = configurationParameters.username;
    this.password = configurationParameters.password;
    this.accessToken = configurationParameters.accessToken;
    this.basePath = configurationParameters.basePath;
    this.withCredentials = configurationParameters.withCredentials;
    this.encoder = configurationParameters.encoder;
    if (configurationParameters.credentials) {
      this.credentials = configurationParameters.credentials;
    } else {
      this.credentials = {};
    }

    // init default bearerAuth credential
    if (!this.credentials['bearerAuth']) {
      this.credentials['bearerAuth'] = () => {
        return typeof this.accessToken === 'function' ? this.accessToken() : this.accessToken;
      };
    }
  }

  /**
   * Select the correct content-type to use for a request.
   * Uses {@link Configuration#isJsonMime} to determine the correct content-type.
   * If no content type is found return the first found type if the contentTypes is not empty
   * @param contentTypes - the array of content types that are available for selection
   * @returns the selected content-type or <code>undefined</code> if no selection could be made.
   */
  public selectHeaderContentType(contentTypes: string[]): string | undefined {
    if (contentTypes.length === 0) {
      return undefined;
    }

    const type = contentTypes.find((x: string) => this.isJsonMime(x));
    if (type === undefined) {
      return contentTypes[0];
    }
    return type;
  }

  /**
   * Select the correct accept content-type to use for a request.
   * Uses {@link Configuration#isJsonMime} to determine the correct accept content-type.
   * If no content type is found return the first found type if the contentTypes is not empty
   * @param accepts - the array of content types that are available for selection.
   * @returns the selected content-type or <code>undefined</code> if no selection could be made.
   */
  public selectHeaderAccept(accepts: string[]): string | undefined {
    if (accepts.length === 0) {
      return undefined;
    }

    const type = accepts.find((x: string) => this.isJsonMime(x));
    if (type === undefined) {
      return accepts[0];
    }
    return type;
  }

  /**
   * Check if the given MIME is a JSON MIME.
   * JSON MIME examples:
   *   application/json
   *   application/json; charset=UTF8
   *   APPLICATION/JSON
   *   application/vnd.company+json
   * @param mime - MIME (Multipurpose Internet Mail Extensions)
   * @return True if the given MIME is JSON, false otherwise.
   */
  public isJsonMime(mime: string): boolean {
    const jsonMime: RegExp = new RegExp('^(application\/json|[^;/ \t]+\/[^;/ \t]+[+]json)[ \t]*(;.*)?$', 'i');
    return mime !== null && (jsonMime.test(mime) || mime.toLowerCase() === 'application/json-patch+json');
  }

  public lookupCredential(key: string): string | undefined {
    const value = this.credentials[key];
    return typeof value === 'function' ? value() : value;
  }
}
