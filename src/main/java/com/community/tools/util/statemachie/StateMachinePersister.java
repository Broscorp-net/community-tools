package com.community.tools.util.statemachie;

import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.kryo.StateMachineContextSerializer;
import org.springframework.stereotype.Component;

@Component
public class StateMachinePersister implements
    StateMachinePersist<State, Event, String> {
  @Autowired
  private StateMachineRepository stateMachineRepository;

  private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
    Kryo kryo = new Kryo();
    kryo.addDefaultSerializer(StateMachineContext.class, new StateMachineContextSerializer());
    return kryo;
  });


  @Override
  public void write(StateMachineContext<State, Event> context, String userID) {
    byte[] data = serialize(context);
    StateEntity stateEntity = new StateEntity();
    stateEntity.setUserID(userID);
    stateEntity.setStateMachine(data);
    stateMachineRepository.save(stateEntity);
  }

  @Override
  public StateMachineContext<State, Event> read(String s) {

    StateEntity stateEntity = stateMachineRepository.findByUserID(s).get();
    byte[] arr = stateEntity.getStateMachine();

    return deserialize(arr);
  }


  private byte[] serialize(StateMachineContext<State, Event> context) {
    Kryo kryo = kryoThreadLocal.get();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Output output = new Output(out);
    kryo.writeClassAndObject(output, context);
    output.flush();
    output.close();
    return out.toByteArray();
  }


  private StateMachineContext<State, Event> deserialize(byte[] data) {
    if (data == null || data.length == 0) {
      return null;
    }
    Kryo kryo = kryoThreadLocal.get();
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    return (StateMachineContext<State, Event>) kryo
        .readClassAndObject(new Input(in));
  }

}
