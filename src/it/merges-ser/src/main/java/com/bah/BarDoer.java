package com.bah;


public final class BarDoer {

  private FooDoer fooDoer;

  public BarDoer(FooDoer fooDoer) {
    this.fooDoer = fooDoer;
  }

  public void doBar() {
    fooDoer.doFoo();
  }

}
