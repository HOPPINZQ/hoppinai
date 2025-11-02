package com.hoppinzq.mcp.filter;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

@Component
public class McpAuthFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (ctx.pathNew().startsWith("/mcp/")) {
            String apiKey = ctx.header("X-API-KEY");
            if(apiKey==null) {
                ctx.status(401);
                return;
            }
        }
        chain.doFilter(ctx);
    }
}
