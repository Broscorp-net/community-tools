package com.community.tools.util.statemachie.actions.transitions.questions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.service.MessageService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.test.util.ReflectionTestUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirstQuestionActionTransitionTest {

  private static final String MESSAGE = "[ {\"type\": \"context\", \"elements\": [{ \"type\": "
      + "\"mrkdwn\", \"text\": \"```"
      + "1. What names should branches and pull requests have?```\" } ] } ]\n";

  @InjectMocks
  private FirstQuestionActionTransition firstQuestionActionTransition;

  @Mock
  private MessageService slackSer;

  @Mock
  private StateContext<State, Event> stateContext;

  @Mock
  private ExtendedState extendedState;

  @Mock
  private SinglePayload singlePayload;

  @BeforeAll
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    Field messageService = FirstQuestionActionTransition.class.getDeclaredField("messageService");
    messageService.setAccessible(true);
    messageService.set(firstQuestionActionTransition, slackSer);

    ReflectionTestUtils.setField(firstQuestionActionTransition, "firstQuestion",
        MESSAGE);
  }

  @Test
  void executeTest() {
    Map<Object, Object> mockData = new HashMap<>();

    Payload payload = new SinglePayload("U01QY6GRZ0X");
    mockData.put("dataPayload", payload);

    when(stateContext.getExtendedState()).thenReturn(extendedState);
    when(extendedState.getVariables()).thenReturn(mockData);
    when(singlePayload.getId()).thenReturn("U01QY6GRZ0X");
    when(slackSer.getUserById("U01QY6GRZ0X")).thenReturn("Илья Либенко");
    when(slackSer.sendBlocksMessage("Илья Либенко", MESSAGE)).thenReturn("");

    firstQuestionActionTransition.execute(stateContext);

    verify(stateContext, times(1)).getExtendedState();
    verify(slackSer, times(1)).getUserById(anyString());
    verify(slackSer, times(1)).sendBlocksMessage(anyString(), anyString());
  }
}