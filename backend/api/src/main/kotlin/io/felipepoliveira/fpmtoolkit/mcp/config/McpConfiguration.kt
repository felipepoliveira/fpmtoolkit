package io.felipepoliveira.fpmtoolkit.mcp.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.felipepoliveira.fpmtoolkit.mcp.tools.WeatherTools
import io.modelcontextprotocol.server.McpServer
import io.modelcontextprotocol.server.McpSyncServer
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider
import io.modelcontextprotocol.spec.McpSchema
import io.modelcontextprotocol.spec.McpServerTransportProvider
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.support.ToolCallbacks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScans
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScans(value = [
    ComponentScan("io.felipepoliveira.fpmtoolkit.mcp")
])
class McpConfiguration @Autowired constructor(
    private val weatherTools: WeatherTools
) {

    @Bean
    fun sseServerTransportProvider() = WebFluxSseServerTransportProvider(
        ObjectMapper(), "/mcp/message"
    )

    @Bean
    fun mcpRouterFunction(transportProvider: WebFluxSseServerTransportProvider) = transportProvider.routerFunction

    @Bean
    fun mcpServer(transportProvider: McpServerTransportProvider): McpSyncServer {
        val capabilities = McpSchema.ServerCapabilities.builder()
            .tools(true)
            .logging()
            .build()

        return McpServer.sync(transportProvider)
            .serverInfo("MCP Demo Weather Server", "1.0.0")
            .capabilities(capabilities)
            .tools(McpToolUtils.toSyncToolSpecification(ToolCallbacks.from(
                weatherTools
            ).toMutableList()))
            .build()
    }

}