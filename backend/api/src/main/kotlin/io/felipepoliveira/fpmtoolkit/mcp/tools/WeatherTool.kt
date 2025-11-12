/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.felipepoliveira.fpmtoolkit.mcp.tools

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class WeatherTools {

    companion object {
        private const val BASE_URL = "https://api.weather.gov"

        @JvmStatic
        fun main(args: Array<String>) {
            val client = WeatherTools()
            println(client.getWeatherForecastByLocation(47.6062, -122.3321))
            println(client.getAlerts("NY"))
        }
    }

    private val restClient: RestClient = RestClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader("Accept", "application/geo+json")
        .defaultHeader("User-Agent", "WeatherApiClient/1.0 (your@email.com)")
        .build()

    // --- Data classes for JSON mapping ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Points(
        @field:JsonProperty("properties") val properties: Props
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Props(
            @field:JsonProperty("forecast") val forecast: String
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Forecast(
        @field:JsonProperty("properties") val properties: Props
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Props(
            @field:JsonProperty("periods") val periods: List<Period>
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Period(
            @field:JsonProperty("number") val number: Int?,
            @field:JsonProperty("name") val name: String?,
            @field:JsonProperty("startTime") val startTime: String?,
            @field:JsonProperty("endTime") val endTime: String?,
            @field:JsonProperty("isDaytime") val isDayTime: Boolean?,
            @field:JsonProperty("temperature") val temperature: Int?,
            @field:JsonProperty("temperatureUnit") val temperatureUnit: String?,
            @field:JsonProperty("temperatureTrend") val temperatureTrend: String?,
            @field:JsonProperty("probabilityOfPrecipitation") val probabilityOfPrecipitation: Map<String, Any>?,
            @field:JsonProperty("windSpeed") val windSpeed: String?,
            @field:JsonProperty("windDirection") val windDirection: String?,
            @field:JsonProperty("icon") val icon: String?,
            @field:JsonProperty("shortForecast") val shortForecast: String?,
            @field:JsonProperty("detailedForecast") val detailedForecast: String?
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Alert(
        @field:JsonProperty("features") val features: List<Feature>
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Feature(
            @field:JsonProperty("properties") val properties: Properties
        ) {
            fun toText(): String = """
                Event: ${properties.event}
                Area: ${properties.areaDesc}
                Severity: ${properties.severity}
                Description: ${properties.description}
                Instructions: ${properties.instruction}
            """.trimIndent()
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Properties(
            @field:JsonProperty("event") val event: String?,
            @field:JsonProperty("areaDesc") val areaDesc: String?,
            @field:JsonProperty("severity") val severity: String?,
            @field:JsonProperty("description") val description: String?,
            @field:JsonProperty("instruction") val instruction: String?
        )
    }

    /**
     * Get forecast for a specific latitude/longitude
     * @param latitude Latitude
     * @param longitude Longitude
     * @return The forecast for the given location
     * @throws RestClientException if the request fails
     */
    @Tool(description = "Get weather forecast for a specific latitude/longitude")
    fun getWeatherForecastByLocation(latitude: Double, longitude: Double): String {
        val points = restClient.get()
            .uri("/points/{latitude},{longitude}", latitude, longitude)
            .retrieve()
            .body(Points::class.java)!!

        val forecast = restClient.get()
            .uri(points.properties.forecast)
            .retrieve()
            .body(Forecast::class.java)!!

        return forecast.properties.periods.joinToString(separator = "\n") { p ->
            """
            ${p.name}:
            Temperature: ${p.temperature} ${p.temperatureUnit}
            Wind: ${p.windSpeed} ${p.windDirection}
            Forecast: ${p.detailedForecast}
            """.trimIndent()
        }
    }

    /**
     * Get alerts for a specific area
     * @param state Area code. Two-letter US state code (e.g. CA, NY)
     * @return Human readable alert information
     * @throws RestClientException if the request fails
     */
    @Tool(description = "Get weather alerts for a US state. Input is Two-letter US state code (e.g. CA, NY)")
    fun getAlerts(state: String): String {
        val alert = restClient.get()
            .uri("/alerts/active/area/{state}", state)
            .retrieve()
            .body(Alert::class.java)!!

        return alert.features.joinToString(separator = "\n\n") { it.toText() }
    }
}
