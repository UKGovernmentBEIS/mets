import { Data } from '@angular/router';

export type RouteBacklink = string | ((data: Data) => string);
