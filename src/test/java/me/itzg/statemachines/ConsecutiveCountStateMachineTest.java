package me.itzg.statemachines;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.itzg.statemachines.ConsecutiveCountStateMachine.StateSpec;
import org.junit.jupiter.api.Test;

class ConsecutiveCountStateMachineTest {

  @Test
  void simple() {
    final ConsecutiveCountStateMachine<String> sm = new ConsecutiveCountStateMachine<>(List.of(
        StateSpec.of("a"),
        StateSpec.of("b"),
        StateSpec.of("c")
    ));

    assertThat(sm.process("a")).isNull();
    assertThat(sm.process("b"))
        .isNotNull()
        .isEqualTo("b");
    assertThat(sm.process("b")).isNull();
    assertThat(sm.process("c"))
        .isNotNull()
        .isEqualTo("c");
  }

  @Test
  void consecutiveCountStates() {
    final ConsecutiveCountStateMachine<String> sm = new ConsecutiveCountStateMachine<>(List.of(
        StateSpec.of("one", 1),
        StateSpec.of("two", 2),
        StateSpec.of("three", 3)
    ));

    // initial state, nothing
    assertThat(sm.process("one")).isNull();

    // two-state, one left
    assertThat(sm.process("two")).isNull();
    // two-state, transitioned
    assertThat(sm.process("two"))
        .isNotNull()
        .isEqualTo("two");

    // repeat current state
    assertThat(sm.process("two")).isNull();

    // transition immediately to one-state
    assertThat(sm.process("one"))
        .isNotNull()
        .isEqualTo("one");

    // three-state, 2 left
    assertThat(sm.process("three")).isNull();
    // three-state, 1 left
    assertThat(sm.process("three")).isNull();
    // three-state, transition
    assertThat(sm.process("three"))
        .isNotNull()
        .isEqualTo("three");

    // repeat current state
    assertThat(sm.process("three")).isNull();
  }
}