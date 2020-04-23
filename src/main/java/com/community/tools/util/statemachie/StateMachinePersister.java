package com.community.tools.util.statemachie;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.kryo.StateMachineContextSerializer;
import org.springframework.stereotype.Component;

@Component
public class StateMachinePersister implements
    StateMachinePersist<State, Event, String> {


  @Value("${DB_URL}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;

  private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
    Kryo kryo = new Kryo();
    kryo.addDefaultSerializer(StateMachineContext.class, new StateMachineContextSerializer());
    return kryo;
  });


  @Override
  public void write(StateMachineContext<State, Event> context, String userID) {
    byte[] data = serialize(context);
    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
    jdbcTemplate.update(
        "UPDATE public.\"StateMachine\" SET state_machine='" + Arrays.toString(data)
            + "'  WHERE context ='" + userID + "';");
  }

  @Override
  public StateMachineContext<State, Event> read(String s) {
    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);

    String state = jdbcTemplate.queryForObject(
        "SELECT state_machine FROM public.\"StateMachine\" WHERE context = '" + s + "';",
        String.class);
    byte[] arr = getBytes(state);

    return deserialize(arr);
  }

  private byte[] getBytes(String state) {
    String[] s = state.replaceAll("[^0-9 \\-]", "").split(" ");
    byte[] bytes = new byte[s.length];
    for (int i = 0; i < s.length; i++) {
      bytes[i] = Byte.parseByte(s[i]);
    }
    return bytes;
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
