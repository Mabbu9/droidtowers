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
 */

package org.apach3.http.impl.client;

import org.apach3.http.annotation.ThreadSafe;
import org.apach3.http.client.HttpClient;
import org.apach3.http.client.protocol.RequestAcceptEncoding;
import org.apach3.http.client.protocol.ResponseContentEncoding;
import org.apach3.http.conn.ClientConnectionManager;
import org.apach3.http.params.HttpParams;
import org.apach3.http.protocol.BasicHttpProcessor;

/**
 * {@link DefaultHttpClient} sub-class which includes a {@link RequestAcceptEncoding}
 * for the request and response.
 *
 * <b>Deprecation note:</b> due to the way this class modifies a response body
 * without changing the response headers to reflect the entity changes, it cannot
 * be used as the &quot;backend&quot; for a caching {@link HttpClient} and still
 * have uncompressed responses be cached. Users are encouraged to use the
 * {@link DecompressingHttpClient} instead of this class, which can be wired in
 * either before or after caching, depending on whether you want to cache
 * responses in compressed or uncompressed form.
 *
 * @since 4.1
 */
@Deprecated
@ThreadSafe // since DefaultHttpClient is
public class ContentEncodingHttpClient extends DefaultHttpClient {

    /**
     * Creates a new HTTP client from parameters and a connection manager.
     *
     * @param params    the parameters
     * @param conman    the connection manager
     */
    public ContentEncodingHttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    /**
     * @param params
     */
    public ContentEncodingHttpClient(HttpParams params) {
        this(null, params);
    }

    /**
     *
     */
    public ContentEncodingHttpClient() {
        this(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        BasicHttpProcessor result = super.createHttpProcessor();

        result.addRequestInterceptor(new RequestAcceptEncoding());
        result.addResponseInterceptor(new ResponseContentEncoding());

        return result;
    }

}
