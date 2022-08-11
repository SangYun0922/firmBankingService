import memoizeOne from 'memoize-one';

import { getState } from '../../../store/store';
import { StoreState } from '../../../types';
import { VariableModel } from '../types';
import { toStateKey } from '../utils';

import { getInitialTemplatingState, TemplatingState } from './reducers';
import { KeyedVariableIdentifier, VariablesState } from './types';

export const getVariable = <T extends VariableModel = VariableModel>(
  identifier: KeyedVariableIdentifier,
  state: StoreState = getState(),
  throwWhenMissing = true
): T => {
  const { id, rootStateKey } = identifier;
  const variablesState = getVariablesState(rootStateKey, state);
  if (!variablesState.variables[id]) {
    if (throwWhenMissing) {
      throw new Error(`Couldn't find variable with id:${id}`);
    }
    return undefined as unknown as T;
  }

  return variablesState.variables[id] as T;
};

function getFilteredVariablesByKey(
  filter: (model: VariableModel) => boolean,
  key: string,
  state: StoreState = getState()
) {
  return Object.values(getVariablesState(key, state).variables)
    .filter(filter)
    .sort((s1, s2) => s1.index - s2.index);
}

export function getVariablesState(key: string, state: StoreState = getState()): TemplatingState {
  return state.templating.keys[toStateKey(key)] ?? getInitialTemplatingState();
}

export function getVariablesByKey(key: string, state: StoreState = getState()): VariableModel[] {
  return getFilteredVariablesByKey(defaultVariablesFilter, key, state);
}

function defaultVariablesFilter(variable: VariableModel): boolean {
  return variable.type !== 'system';
}

export const getSubMenuVariables = memoizeOne(
  (key: string, variables: Record<string, VariableModel>): VariableModel[] => {
    return getVariablesByKey(key, getState());
  }
);

export const getEditorVariables = (key: string, state: StoreState): VariableModel[] => {
  return getVariablesByKey(key, state);
};

export type GetVariables = typeof getVariablesByKey;

export function getNewVariableIndex(key: string, state: StoreState = getState()): number {
  return getNextVariableIndex(Object.values(getVariablesState(key, state).variables));
}

export function getNextVariableIndex(variables: VariableModel[]): number {
  const sorted = variables.filter(defaultVariablesFilter).sort((v1, v2) => v1.index - v2.index);
  return sorted.length > 0 ? sorted[sorted.length - 1].index + 1 : 0;
}

export function getVariablesIsDirty(key: string, state: StoreState = getState()): boolean {
  return getVariablesState(key, state).transaction.isDirty;
}

export function getIfExistsLastKey(state: StoreState = getState()): string | undefined {
  return state.templating?.lastKey;
}

export function getLastKey(state: StoreState = getState()): string {
  if (!state.templating?.lastKey) {
    throw new Error('Accessing lastKey without initializing it variables');
  }

  return state.templating.lastKey;
}

// selectors used by template srv, assumes that lastKey is in state. Needs to change when/if dashboard redux state becomes keyed too.
export function getFilteredVariables(filter: (model: VariableModel) => boolean, state: StoreState = getState()) {
  const lastKey = getIfExistsLastKey(state);
  if (!lastKey) {
    return [];
  }
  return getFilteredVariablesByKey(filter, lastKey, state);
}

export function getVariables(state: StoreState = getState()) {
  const lastKey = getIfExistsLastKey(state);
  if (!lastKey) {
    return [];
  }
  return getVariablesByKey(lastKey, state);
}

export function getVariableWithName(name: string, state: StoreState = getState()) {
  const lastKey = getIfExistsLastKey(state);
  if (!lastKey) {
    return;
  }
  return getVariable({ id: name, rootStateKey: lastKey, type: 'query' }, state, false);
}

export function getInstanceState<Model extends VariableModel = VariableModel>(state: VariablesState, id: string) {
  return state[id] as Model;
}
