import { Injectable } from '@angular/core';

import { AccountClosureActionStoreDelegate } from '@aviation/request-action/store/delegates';
import { AerActionStoreDelegate } from '@aviation/request-action/store/delegates/aer';
import { AerCorsiaActionStoreDelegate } from '@aviation/request-action/store/delegates/aer-corsia';
import { VirActionStoreDelegate } from '@aviation/request-action/store/delegates/vir';
import { selectType, TypeAwareStore } from '@aviation/type-aware.store';
import { Store } from '@core/store';

import { RequestActionDTO } from 'pmrv-api';

import { EmpActionStoreDelegate } from './delegates';
import { DoeActionStoreDelegate } from './delegates/doe';
import { DreActionStoreDelegate } from './delegates/dre';
import { initialState, RequestActionState } from './request-action.state';

@Injectable({ providedIn: 'root' })
export class RequestActionStore extends Store<RequestActionState> implements TypeAwareStore {
  private _empDelegate: EmpActionStoreDelegate;
  private _accountClosureDelegate: AccountClosureActionStoreDelegate;
  private _dreDelegate: DreActionStoreDelegate;
  private _aerDelegate: AerActionStoreDelegate | AerCorsiaActionStoreDelegate;
  private _virDelegate: VirActionStoreDelegate;
  private _doeDelegate: DoeActionStoreDelegate;

  constructor() {
    super(initialState);
  }

  get type$() {
    return this.pipe(selectType);
  }

  get empDelegate() {
    if (!this._empDelegate) {
      this._empDelegate = new EmpActionStoreDelegate(this);
    }
    return this._empDelegate;
  }

  get accountClosureDelegate() {
    if (!this._accountClosureDelegate) {
      this._accountClosureDelegate = new AccountClosureActionStoreDelegate(this);
    }
    return this._accountClosureDelegate;
  }

  get dreDelegate() {
    if (!this._dreDelegate) {
      this._dreDelegate = new DreActionStoreDelegate(this);
    }
    return this._dreDelegate;
  }

  get doeDelegate() {
    if (!this._doeDelegate) {
      this._doeDelegate = new DoeActionStoreDelegate(this);
    }
    return this._doeDelegate;
  }

  get aerDelegate() {
    if (!this._aerDelegate) {
      this._aerDelegate =
        this.getState().requestActionItem?.requestType === 'AVIATION_AER_CORSIA'
          ? new AerCorsiaActionStoreDelegate(this)
          : new AerActionStoreDelegate(this);
    }
    return this._aerDelegate;
  }

  get virDelegate() {
    if (!this._virDelegate) {
      this._virDelegate = new VirActionStoreDelegate(this);
    }
    return this._virDelegate;
  }

  destroyDelegates() {
    this._empDelegate = null;
    this._accountClosureDelegate = null;
    this._dreDelegate = null;
    this._aerDelegate = null;
    this._virDelegate = null;
    this._doeDelegate = null;
  }

  setRequestActionItem(requestActionItem: RequestActionDTO) {
    this.setState({ ...this.getState(), requestActionItem });
  }

  setRegulatorViewer(regulatorViewer: boolean) {
    this.setState({ ...this.getState(), regulatorViewer });
  }
}
