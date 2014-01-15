package com.bah;

import java.io.File;

import mockit.integration.junit4.JMockit;
import mockit.Mocked;
import mockit.Expectations;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class SimpleTest {

  @Test
  public void doFoo(final @Mocked FooDoer fooDoer) {
    new Expectations(){{
      fooDoer.doFoo(); 
    }};
    new BarDoer(fooDoer).doBar();
  }
}
