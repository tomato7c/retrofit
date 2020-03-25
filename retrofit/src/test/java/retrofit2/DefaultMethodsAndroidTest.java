/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import retrofit2.helpers.ToStringConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.robolectric.annotation.Config.NEWEST_SDK;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = NEWEST_SDK, manifest = NONE)
public final class DefaultMethodsAndroidTest {
  @Rule public final MockWebServer server = new MockWebServer();

  interface Example {
    @GET("/") Call<String> user(@Query("name") String name);

    default Call<String> user() {
      return user("hey");
    }
  }

  @Config(sdk = 24)
  @Test public void failsOnApi24() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new ToStringConverterFactory())
        .build();
    Example example = retrofit.create(Example.class);

    try {
      example.user();
      fail();
    } catch (UnsupportedOperationException e) {
      assertThat(e).hasMessage("Calling default methods on API 24 and 25 is not supported");
    }
  }

  @Config(sdk = 25)
  @Test public void failsOnApi25() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new ToStringConverterFactory())
        .build();
    Example example = retrofit.create(Example.class);

    try {
      example.user();
      fail();
    } catch (UnsupportedOperationException e) {
      assertThat(e).hasMessage("Calling default methods on API 24 and 25 is not supported");
    }
  }

  /**
   * Notably, this does not test that it works correctly on API 26+. Merely that the special casing
   * of API 24/25 does not trigger.
   */
  @Test public void doesNotFailOnApi26() throws IOException {
    server.enqueue(new MockResponse().setBody("Hi"));
    server.enqueue(new MockResponse().setBody("Hi"));

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new ToStringConverterFactory())
        .build();
    Example example = retrofit.create(Example.class);

    Response<String> response = example.user().execute();
    assertThat(response.body()).isEqualTo("Hi");
    Response<String> response2 = example.user("Hi").execute();
    assertThat(response2.body()).isEqualTo("Hi");
  }
}