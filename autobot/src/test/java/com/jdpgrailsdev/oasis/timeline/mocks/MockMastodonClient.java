package com.jdpgrailsdev.oasis.timeline.mocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;

/** Mock version of the {@link MastodonClient} class that provides a specific test response. */
public class MockMastodonClient {

  /**
   * Constructs a mocked version of the {@link MastodonClient} class.
   *
   * @return A mocked {@link MastodonClient} instance.
   */
  public static MastodonClient mock() {
    final MastodonClient mastodonClient = Mockito.mock(MastodonClient.class);
    stubResponse(mastodonClient, HttpStatus.OK);
    return mastodonClient;
  }

  /**
   * Constructs a mocked version of the {@link MastodonClient} class that throws an exception when
   * invoked.
   *
   * @return A mocked {@link MastodonClient} instance.
   */
  public static MastodonClient mockWithException() {
    final MastodonClient mastodonClient = Mockito.mock(MastodonClient.class);
    stubResponse(mastodonClient, HttpStatus.INTERNAL_SERVER_ERROR);
    return mastodonClient;
  }

  private static void stubResponse(final MastodonClient client, final HttpStatus httpStatus) {
    when(client.get(anyString(), eq(null))).thenAnswer(new MastodonResponseAnswer(httpStatus));
    when(client.get(anyString(), any())).thenAnswer(new MastodonResponseAnswer(httpStatus));
    when(client.post(anyString(), any())).thenAnswer(new MastodonResponseAnswer(httpStatus));
    when(client.postUrl(anyString(), any())).thenAnswer(new MastodonResponseAnswer(httpStatus));
    when(client.patch(anyString(), any())).thenAnswer(new MastodonResponseAnswer(httpStatus));
    when(client.getSerializer()).thenReturn(new Gson());
  }

  private static Response createResponse(final HttpStatus httpStatus) throws IOException {
    final String bodyContent =
        Resources.toString(
            Resources.getResource("json/mastodon_status.json"), StandardCharsets.UTF_8);
    return new Response.Builder()
        .code(httpStatus.value())
        .message("test")
        .request(new Request.Builder().url("https://test.com/").build())
        .protocol(Protocol.HTTP_1_1)
        .body(ResponseBody.create(bodyContent, MediaType.parse("application/json; charset=utf-8")))
        .build();
  }

  private record MastodonResponseAnswer(HttpStatus httpStatus) implements Answer {

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
      return createResponse(httpStatus);
    }
  }
}
