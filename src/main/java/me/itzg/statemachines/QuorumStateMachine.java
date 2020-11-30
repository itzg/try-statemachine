package me.itzg.statemachines;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a state-machine that maintains a {@link StateHolder} instance per entry
 * of type {@link E} where requested, quorum number of entries need to enter a new state for the
 * overall state to transition.
 * @param <S> type of state value
 * @param <E> type of quorum entry identifier
 */
public class QuorumStateMachine<S,E> {

  private final int quorum;
  private final StateHolderFactory<S> stateHolderFactory;
  private final Map<E, StateHolder<S>> entries = new ConcurrentHashMap<>();
  private final DefaultStateHolder<S> overall = new DefaultStateHolder<>();

  public QuorumStateMachine(int quorum,
                            StateHolderFactory<S> stateHolderFactory) {
    if (quorum <= 0) {
      throw new IllegalArgumentException("quorum must be greater than zero");
    }
    this.quorum = quorum;
    this.stateHolderFactory =
        stateHolderFactory != null ? stateHolderFactory : DefaultStateHolder::new;
  }

  public S process(E entry, S input) {
    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null");
    }
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null");
    }

    final StateHolder<S> entryState = entries.computeIfAbsent(entry,
        e -> stateHolderFactory.create());

    final S result = entryState.process(input);
    if (result != null) {
      return evaluateQuorum(result);
    } else {
      return null;
    }
  }

  private S evaluateQuorum(S newState) {
    final long countInNewState = entries.values().stream()
        .map(StateHolder::getState)
        .filter(s -> s.equals(newState))
        .count()
        ;

    if (countInNewState >= quorum) {
      return overall.process(newState);
    } else {
      return null;
    }
  }

}
