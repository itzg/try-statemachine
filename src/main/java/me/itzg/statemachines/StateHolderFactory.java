package me.itzg.statemachines;

@FunctionalInterface
public interface StateHolderFactory<S> {
  StateHolder<S> create();
}
