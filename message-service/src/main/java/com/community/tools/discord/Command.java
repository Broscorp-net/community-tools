package com.community.tools.discord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

  /**
   * Command's name.
   * @return command's name
   */
  String name();

  /**
   * Command's description.
   * @return command's description
   */
  String description();

  /**
   * Array of options' names. Length of the array must match the others.
   * @return array of options
   */
  String[] options() default "";

  /**
   * Array of options' types. Length of the array must match the others.
   * @return array of options' types
   */
  OptionType[] optionTypes() default {};

  /**
   * Array of options' descriptions. Length of the array must match the others.
   * @return array of options' descriptions
   */
  String[] optionsDescriptions() default "";

  /**
   * Array of options' requirement state, where true - option is required.
   * Length of the array must match the others.
   * @return array of options' requirement
   */
  boolean[] optionsRequirements() default {};

}
