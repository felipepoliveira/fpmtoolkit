package io.felipepoliveira.fpmtoolkit.api.controllers

import org.springframework.http.ResponseEntity

typealias EmptyResponseBuilderCallback = (responseBuilder: ResponseEntity.BodyBuilder) -> Unit
typealias ResponseBuilderCallbackWithBody = (responseBuilder: ResponseEntity.BodyBuilder) -> Any
typealias OptionalResponseBuilderCallback = (responseBuilder: ResponseEntity.BodyBuilder) -> Any?

abstract class BaseRestController {

    /**
     * Functional method that increase the syntax quality for @RestController endpoints. Send an 204 NO CONTENT response
     * with no response body
     */
    fun noContent(callback: EmptyResponseBuilderCallback) = send(204, callback)

    /**
     * Functional method that increase the syntax quality for @RestController endpoints. Send an 200 OK response
     * with a body
     */
    fun ok(callback: ResponseBuilderCallbackWithBody) = send(200, callback)

    /**
     * Send a redirect request (302) to the given location
     */
    fun redirect(location: String, callback: EmptyResponseBuilderCallback) = send(302) { reqBuilder ->
        reqBuilder.header("Location", location)
        callback(reqBuilder)
    }

    /**
     * Send a redirect request (302) to the given location
     */
    fun redirect(location: String): ResponseEntity<Any> {
        return send(302) { reqBuilder ->
            reqBuilder.header("Location", location)
        }
    }

    /**
     * Functional method that increase the syntax quality for @RestController endpoints.
     */
    fun send(responseCode: Int, callback: OptionalResponseBuilderCallback): ResponseEntity<Any> {
        val responseBuilder = ResponseEntity.status(responseCode)
        val body = callback(responseBuilder)
        return if (body != null) {
            responseBuilder.body(body)
        } else {
            responseBuilder.build()
        }
    }

}