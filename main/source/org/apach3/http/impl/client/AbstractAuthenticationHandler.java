/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apach3.http.impl.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apach3.commons.logging.Log;
import org.apach3.commons.logging.LogFactory;
import org.apach3.http.FormattedHeader;
import org.apach3.http.Header;
import org.apach3.http.HttpResponse;
import org.apach3.http.annotation.Immutable;
import org.apach3.http.auth.AuthScheme;
import org.apach3.http.auth.AuthSchemeRegistry;
import org.apach3.http.auth.AuthenticationException;
import org.apach3.http.auth.MalformedChallengeException;
import org.apach3.http.client.AuthenticationHandler;
import org.apach3.http.client.AuthenticationStrategy;
import org.apach3.http.client.params.AuthPolicy;
import org.apach3.http.client.protocol.ClientContext;
import org.apach3.http.protocol.HTTP;
import org.apach3.http.protocol.HttpContext;
import org.apach3.http.util.CharArrayBuffer;

/**
 * Base class for {@link AuthenticationHandler} implementations.
 *
 * @since 4.0
 *
 * @deprecated (4.2)  use {@link AuthenticationStrategy}
 */
@Deprecated
@Immutable
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {

    private final Log log = LogFactory.getLog(getClass());

    private static final List<String> DEFAULT_SCHEME_PRIORITY =
        Collections.unmodifiableList(Arrays.asList(new String[] {
                AuthPolicy.SPNEGO,
                AuthPolicy.NTLM,
                AuthPolicy.DIGEST,
                AuthPolicy.BASIC
    }));

    public AbstractAuthenticationHandler() {
        super();
    }

    protected Map<String, Header> parseChallenges(
            final Header[] headers) throws MalformedChallengeException {

        Map<String, Header> map = new HashMap<String, Header>(headers.length);
        for (Header header : headers) {
            CharArrayBuffer buffer;
            int pos;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader) header).getBuffer();
                pos = ((FormattedHeader) header).getValuePos();
            } else {
                String s = header.getValue();
                if (s == null) {
                    throw new MalformedChallengeException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            int beginIndex = pos;
            while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            int endIndex = pos;
            String s = buffer.substring(beginIndex, endIndex);
            map.put(s.toLowerCase(Locale.US), header);
        }
        return map;
    }

    /**
     * Returns default list of auth scheme names in their order of preference.
     *
     * @return list of auth scheme names
     */
    protected List<String> getAuthPreferences() {
        return DEFAULT_SCHEME_PRIORITY;
    }

    /**
     * Returns default list of auth scheme names in their order of preference
     * based on the HTTP response and the current execution context.
     *
     * @param response HTTP response.
     * @param context HTTP execution context.
     *
     * @since 4.1
     */
    protected List<String> getAuthPreferences(
            final HttpResponse response,
            final HttpContext context) {
        return getAuthPreferences();
    }

    public AuthScheme selectScheme(
            final Map<String, Header> challenges,
            final HttpResponse response,
            final HttpContext context) throws AuthenticationException {

        AuthSchemeRegistry registry = (AuthSchemeRegistry) context.getAttribute(
                ClientContext.AUTHSCHEME_REGISTRY);
        if (registry == null) {
            throw new IllegalStateException("AuthScheme registry not set in HTTP context");
        }

        Collection<String> authPrefs = getAuthPreferences(response, context);
        if (authPrefs == null) {
            authPrefs = DEFAULT_SCHEME_PRIORITY;
        }

        if (this.log.isDebugEnabled()) {
            this.log.debug("Authentication schemes in the order of preference: "
                + authPrefs);
        }

        AuthScheme authScheme = null;
        for (String id: authPrefs) {
            Header challenge = challenges.get(id.toLowerCase(Locale.ENGLISH));

            if (challenge != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(id + " authentication scheme selected");
                }
                try {
                    authScheme = registry.getAuthScheme(id, response.getParams());
                    break;
                } catch (IllegalStateException e) {
                    if (this.log.isWarnEnabled()) {
                        this.log.warn("Authentication scheme " + id + " not supported");
                        // Try again
                    }
                }
            } else {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Challenge for " + id + " authentication scheme not available");
                    // Try again
                }
            }
        }
        if (authScheme == null) {
            // If none selected, something is wrong
            throw new AuthenticationException(
                "Unable to respond to any of these challenges: "
                    + challenges);
        }
        return authScheme;
    }

}
