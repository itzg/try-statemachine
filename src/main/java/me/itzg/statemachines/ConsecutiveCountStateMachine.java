package me.itzg.statemachines;

import java.util.List;

/**
 * Implements a state machine where a given state needs to be observed a defined number of times
 * in a row before the overall state transitions there.
 * <p>
 * <img src="consecutive-count-state-machine.drawio.png"/>
 * </p>
 * @param <S> type of state value
 */
public class ConsecutiveCountStateMachine<S> implements StateHolder<S> {

  final List<StateSpec<S>> specs;
  StateSpec<S> currentState;
  PendingState<S> pendingState;

  public ConsecutiveCountStateMachine(List<StateSpec<S>> specs) {
    if (specs == null || specs.isEmpty()) {
      throw new IllegalArgumentException("specs must be non-empty");
    }
    this.specs = specs;
    currentState = specs.get(0);
  }

  public S process(S input) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null");
    }
    if (input.equals(currentState.name)) {
      pendingState = null;
      return null;
    }

    if (pendingState == null || !input.equals(pendingState.spec.name)) {
      final StateSpec<S> next = specs.stream()
          .filter(stateSpec -> stateSpec.name.equals(input))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unknown state name"));

      pendingState = new PendingState<S>(next);
    }

    --pendingState.remainder;

    if (pendingState.remainder <= 0) {
      currentState = pendingState.spec;
      pendingState = null;
      return currentState.name;
    } else {
      return null;
    }
  }

  @Override
  public S getState() {
    return currentState.getName();
  }

  public static class StateSpec<S> {

    final S name;
    final int consecutiveCount;

    private StateSpec(S name, int consecutiveCount) {
      if (name == null) {
        throw new IllegalArgumentException("name is required");
      }
      if (consecutiveCount < 1) {
        throw new IllegalArgumentException("consecutiveCount must be greater than 0");
      }
      this.name = name;
      this.consecutiveCount = consecutiveCount;
    }

    public static <S> StateSpec<S> of(S name) {
      return new StateSpec<>(name, 1);
    }

    public static <S> StateSpec<S> of(S name, int consecutiveCount) {
      return new StateSpec<>(name, consecutiveCount);
    }

    public S getName() {
      return name;
    }
  }

  static class PendingState<S> {

    StateSpec<S> spec;
    int remainder;

    public PendingState(StateSpec<S> spec) {
      this.spec = spec;
      remainder = spec.consecutiveCount;
    }

  }

}