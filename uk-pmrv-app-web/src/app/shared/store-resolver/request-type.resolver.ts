import { UrlRequestType, urlRequestTypes } from '../types/url-request-type';

export function resolveRequestType(url: string): UrlRequestType {
  return urlRequestTypes.find((urlRequestType) => url.includes(urlRequestType));
}
