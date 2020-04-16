package com.community.tools.util.statemachie;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;


public class PurchaseStateMachinePersister implements
    StateMachinePersist<PurchaseState, PurchaseEvent, String> {
  @Autowired
  private StateMachineFactory<PurchaseState, PurchaseEvent> factory;
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;

  // TODO: 15.04.2020 сделаь запись в базу данных а не в hashMap
  private final HashMap<String, StateMachineContext<PurchaseState, PurchaseEvent>> contexts =
      new HashMap<>();

  @Override
  public void write(StateMachineContext<PurchaseState, PurchaseEvent> context, String userID)
      throws Exception {

    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);

    jdbcTemplate.update(
        "INSERT INTO public.\"StateMachine\" (state_machine, context) VALUES ('" + context.getState() + "','"
            + userID + "');");

    System.out.println(context.getState());
     // contexts.put(userID,context);
  }

  @Override
  public StateMachineContext<PurchaseState, PurchaseEvent> read(String s) throws Exception {
    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
    PurchaseState state = jdbcTemplate.queryForObject(
        "SELECT ALL FROM public.\"StateMachine\" WHERE context = " + s,PurchaseState.class);
    // TODO: 16.04.2020 инициализировать и вернуть стэйт машин контекст
    StateMachineContext<PurchaseState, PurchaseEvent> machine = null;

    System.out.println(state.name());
    return machine;
  }
}
