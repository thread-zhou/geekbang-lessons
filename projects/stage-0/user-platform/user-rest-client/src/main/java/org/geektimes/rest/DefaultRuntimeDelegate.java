package org.geektimes.rest;

import org.geektimes.rest.client.DefaultVariantListBuilder;
import org.geektimes.rest.core.DefaultResponseBuilder;
import org.geektimes.rest.core.DefaultUriBuilder;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @ClassName: DefaultRuntimeDelegate
 * @Description: {@link RuntimeDelegate} Default Implementation
 *
 * 大管家
 *
 * @author: zhoujian
 * @date: 2021/3/27 18:23
 * @version: 1.0
 */
public class DefaultRuntimeDelegate extends RuntimeDelegate {

    @Override
    public UriBuilder createUriBuilder() {
        return new DefaultUriBuilder();
    }

    @Override
    public Response.ResponseBuilder createResponseBuilder() {
        return new DefaultResponseBuilder();
    }

    @Override
    public Variant.VariantListBuilder createVariantListBuilder() {
        return new DefaultVariantListBuilder();
    }

    @Override
    public <T> T createEndpoint(Application application, Class<T> aClass) throws IllegalArgumentException, UnsupportedOperationException {
        return null;
    }

    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> aClass) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Link.Builder createLinkBuilder() {
        return null;
    }
}
