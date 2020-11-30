package me.itzg.statemachines;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuorumStateMachineTest {

  @Test
  void processPartialQuorum() {
    QuorumStateMachine<String,String> sm = new QuorumStateMachine<>(
        2,
        DefaultStateHolder::new
    );

    // ZoneA | ZoneB | ZoneC | ZoneD -> internal state

    // *CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    // CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    // CRITICAL | *CRITICAL | ? | ? -> CRITICAL
    assertThat(sm.process("zoneB", "CRITICAL"))
        .isNotNull()
        .isEqualTo("CRITICAL");
    // CRITICAL | CRITICAL | *CRITICAL | ? -> CRITICAL
    assertThat(sm.process("zoneC", "CRITICAL"))
        .isNull();
    // CRITICAL | CRITICAL | CRITICAL | *WARNING -> CRITICAL
    assertThat(sm.process("zoneD", "WARNING"))
        .isNull();
    // *WARNING | CRITICAL | CRITICAL | WARNING -> WARNING
    assertThat(sm.process("zoneA", "WARNING"))
        .isNotNull()
        .isEqualTo("WARNING");
  }

  @Test
  void processQuorumOfOne() {
    QuorumStateMachine<String,String> sm = new QuorumStateMachine<>(
        1,
        DefaultStateHolder::new
    );

    // ZoneA | ZoneB | ZoneC | ZoneD -> internal state

    // *CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNotNull()
        .isEqualTo("CRITICAL");
    // CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    // CRITICAL | *CRITICAL | ? | ? -> CRITICAL
    assertThat(sm.process("zoneB", "CRITICAL"))
        .isNull();
    // CRITICAL | CRITICAL | *CRITICAL | ? -> CRITICAL
    assertThat(sm.process("zoneC", "CRITICAL"))
        .isNull();
    // CRITICAL | CRITICAL | CRITICAL | *WARNING -> WARNING
    assertThat(sm.process("zoneD", "WARNING"))
        .isNotNull()
        .isEqualTo("WARNING");
    // *WARNING | CRITICAL | CRITICAL | WARNING -> WARNING
    assertThat(sm.process("zoneA", "WARNING"))
        .isNull();
  }

  @Test
  void processQuorumOfAll() {
    QuorumStateMachine<String,String> sm = new QuorumStateMachine<>(
        4,
        DefaultStateHolder::new
    );

    // ZoneA | ZoneB | ZoneC | ZoneD -> internal state

    // *CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    // CRITICAL | ? | ? | ? -> ?
    assertThat(sm.process("zoneA", "CRITICAL"))
        .isNull();
    // CRITICAL | *CRITICAL | ? | ? -> ?
    assertThat(sm.process("zoneB", "CRITICAL"))
        .isNull();
    // CRITICAL | CRITICAL | *CRITICAL | ? -> ?
    assertThat(sm.process("zoneC", "CRITICAL"))
        .isNull();
    // CRITICAL | CRITICAL | CRITICAL | *WARNING -> ?
    assertThat(sm.process("zoneD", "WARNING"))
        .isNull();
    // CRITICAL | CRITICAL | CRITICAL | *CRITICAL -> CRITICAL
    assertThat(sm.process("zoneD", "CRITICAL"))
        .isNotNull()
        .isEqualTo("CRITICAL");
    // *WARNING | CRITICAL | CRITICAL | CRITICAL -> CRITICAL
    assertThat(sm.process("zoneA", "WARNING"))
        .isNull();
  }
}