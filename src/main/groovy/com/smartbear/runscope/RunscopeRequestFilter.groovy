package com.smartbear.runscope

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.submit.filters.AbstractRequestFilter;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.SubmitContext;

public class RunscopeRequestFilter extends AbstractRequestFilter
{
    @Override
    public void filterRequest(SubmitContext context, Request request) {
        def bucket = context.expand( '${#Project#RunscopeBucket}' )
        if( bucket.length() > 0 )
        {
            def uri = context.httpMethod.URI
            def hn = uri.host

            if( context.expand( '${#Project#RunscopeCapture}' ) != "true" )
                hn = hn.replaceAll( "-", "--" ).replaceAll( "\\.", "-" ) + "-" + bucket;
            else
                hn = bucket

            hn += ".runscope.net"

            SoapUI.log.info "changed $uri.host to $hn"

            def str = uri.toString()
            def ix = str.indexOf( uri.host )
            def newUri = str.substring( 0, ix ) + hn + str.substring( ix + uri.host.length())

            SoapUI.log.info "new URI is $newUri"

            context.httpMethod.URI = java.net.URI.create( newUri )
        }
    }
}
