package com.smartbear.runscope

import com.eviware.soapui.impl.wsdl.submit.filters.AbstractRequestFilter
import com.eviware.soapui.model.iface.Request
import com.eviware.soapui.model.iface.SubmitContext

/**
 * A RequestFilter that modified the endpoint of outgoing requests in line with the Runscope
 * documentation if a correspondign RunscopeBucket has been set at the project level.
 */

public class RunscopeRequestFilter extends AbstractRequestFilter {
    @Override
    public void filterRequest(SubmitContext context, Request request) {

        // check for bucket id
        def bucket = context.expand('${#Project#RunscopeBucket}')
        if (bucket.length() > 0) {
            def uri = context.httpMethod.URI
            def hn = uri.host

            // modify the hostname
            if (context.expand('${#Project#RunscopeCapture}') != "true")
                hn = hn.replaceAll("-", "--").replaceAll("\\.", "-") + "-" + bucket;
            else
                hn = bucket

            hn += ".runscope.net"

            // build new uri
            def str = uri.toString()
            def ix = str.indexOf(uri.host)
            def newUri = str.substring(0, ix) + hn + str.substring(ix + uri.host.length())

            // save back to context
            context.httpMethod.URI = java.net.URI.create(newUri)
        }
    }
}
