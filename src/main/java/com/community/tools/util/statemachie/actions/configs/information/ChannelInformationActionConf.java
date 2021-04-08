package com.community.tools.util.statemachie.actions.configs.information;

import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.stereotype.Component;

import static com.community.tools.util.statemachie.Event.CHANNELS_INFORMATION;
import static com.community.tools.util.statemachie.State.INFORMATION_CHANNELS;
import static com.community.tools.util.statemachie.State.THIRD_QUESTION;

@Component
public class ChannelInformationActionConf {

  private Action<State, Event> informationChannelsAction;
  private Action<State, Event> errorAction;

  @Autowired
  public ChannelInformationActionConf(Action<State, Event> informationChannelsAction,
                                      Action<State, Event> errorAction) {
    this.informationChannelsAction = informationChannelsAction;
    this.errorAction = errorAction;
  }

  public ExternalTransitionConfigurer<State, Event> configure(ExternalTransitionConfigurer<State, Event> transition) throws Exception {
    return transition
        .and()
        .withExternal()
        .source(THIRD_QUESTION)
        .target(INFORMATION_CHANNELS)
        .event(CHANNELS_INFORMATION)
        .action(informationChannelsAction, errorAction);
  }
}
