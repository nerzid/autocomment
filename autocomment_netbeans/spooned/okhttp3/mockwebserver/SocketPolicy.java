/**
 * Copyright (C) 2011 Google Inc.
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


package okhttp3.mockwebserver;


/**
 * What should be done with the incoming socket.
 *
 * <p>Be careful when using values like {@link #DISCONNECT_AT_END}, {@link #SHUTDOWN_INPUT_AT_END}
 * and {@link #SHUTDOWN_OUTPUT_AT_END} that close a socket after a response, and where there are
 * follow-up requests. The client is unblocked and free to continue as soon as it has received the
 * entire response body. If and when the client makes a subsequent request using a pooled socket the
 * server may not have had time to close the socket. The socket will be closed at an indeterminate
 * point before or during the second request. It may be closed after client has started sending the
 * request body. If a request body is not retryable then the client may fail the request, making
 * client behavior non-deterministic. Add delays in the client to improve the chances that the
 * server has closed the socket before follow up requests are made.
 */
public enum SocketPolicy {
KEEP_OPEN, DISCONNECT_AT_END, UPGRADE_TO_SSL_AT_END, DISCONNECT_AT_START, DISCONNECT_AFTER_REQUEST, DISCONNECT_DURING_REQUEST_BODY, DISCONNECT_DURING_RESPONSE_BODY, FAIL_HANDSHAKE, SHUTDOWN_INPUT_AT_END, SHUTDOWN_OUTPUT_AT_END, NO_RESPONSE, RESET_STREAM_AT_START;}

