import { computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { BehaviorSubject, map, Observable } from 'rxjs';

export abstract class Store<T> extends BehaviorSubject<T> {
  private _stateSignal?: Signal<T>;

  protected constructor(protected readonly initialState?: T) {
    super(initialState);
  }

  getState(): T {
    return this.getValue();
  }

  setState(state: T): void {
    this.next(state);
  }

  //eslint-disable-next-line @typescript-eslint/no-unused-vars
  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    return [];
  }

  select<K extends keyof T>(name: K): Observable<T[K]> {
    return this.pipe(map((x) => x?.[name]));
  }

  // Lazily converts to a signal on first use, so stores constructed outside an injection context
  // (e.g. plain `new SomeStore()` in tests) never trigger toSignal() unless selectSignal() is actually called.
  selectSignal<K extends keyof T>(name: K): Signal<T[K]> {
    this._stateSignal ??= toSignal(this, { requireSync: true });
    return computed(() => this._stateSignal()?.[name]);
  }

  reset(): void {
    this.setState(this.initialState);
  }
}
