package me.itzg.statemachines;

import java.util.Objects;

public class DefaultStateHolder<S> implements StateHolder<S> {

  S state;

  @Override
  public S process(S input) {
    if (!Objects.equals(state, input)) {
      state = input;
      return state;
    }
    return null;
  }

  @Override
  public S getState() {
    return state;
  }
}
