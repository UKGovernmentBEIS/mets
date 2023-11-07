import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class RefreshResolver implements Resolve<null> {
  resolve(): null {
    return null;
  }
}
