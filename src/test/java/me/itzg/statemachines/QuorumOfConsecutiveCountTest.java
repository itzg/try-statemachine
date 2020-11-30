package me.itzg.statemachines;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import me.itzg.statemachines.ConsecutiveCountStateMachine.StateSpec;
import org.junit.jupiter.api.Test;

public class QuorumOfConsecutiveCountTest {

  @Test
  void combination() {
    var stateSpecs = List.of(
        StateSpec.of("OK", 1),
        StateSpec.of("CRITICAL", 2)
    );

    final QuorumStateMachine<String, String> sm = new QuorumStateMachine<>(
        2,
        () -> new ConsecutiveCountStateMachine<>(stateSpecs)
    );

    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    assertThat(sm.process("zoneB", "CRITICAL"))
        .isNull();
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    assertThat(sm.process("zoneB", "CRITICAL"))
        .isNotNull()
        .isEqualTo("CRITICAL");
    assertThat(sm.process("zoneA", "OK"))
        .isNull();
    assertThat(sm.process("zoneB", "OK"))
        .isNotNull()
        .isEqualTo("OK");
  }
}
