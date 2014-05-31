/*
 * Copyright 2014 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package org.vertx.java.tests.newtests;

import org.junit.Test;
import org.vertx.java.core.Context;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ContextTest extends VertxTestBase {

  @Test
  public void testRunOnContext() throws Exception {
    vertx.runOnContext(v -> {
      Thread th = Thread.currentThread();
      Context ctx = vertx.currentContext();
      ctx.runOnContext(v2 -> {
        assertEquals(th, Thread.currentThread());
        assertEquals(ctx, vertx.currentContext());
        // And simulate a third party thread - e.g. a 3rd party async library wishing to return a result on the
        // correct context
        new Thread() {
          public void run() {
            ctx.runOnContext(v3 -> {
              assertEquals(th, Thread.currentThread());
              assertEquals(ctx, vertx.currentContext());
              testComplete();
            });
          }
        }.start();
      });
    });
    await();
  }

  @Test
  public void testNoContext() throws Exception {
    assertNull(vertx.currentContext());
    testComplete();
  }
}