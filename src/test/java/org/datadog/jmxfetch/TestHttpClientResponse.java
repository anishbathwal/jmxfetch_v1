package org.datadog.jmxfetch;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestHttpClientResponse {

    // === Positive Tests ===

    @Test
    public void testConstructorWithCodeAndBody() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, "OK");
        assertEquals(200, response.getResponseCode());
        assertEquals("OK", response.getResponseBody());
    }

    @Test
    public void testConstructorWithInputStream() throws IOException {
        String body = "Hello World";
        ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(bais);
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, reader);
        assertEquals(200, response.getResponseCode());
        assertEquals("Hello World", response.getResponseBody());
    }

    @Test
    public void testConstructorWithMultiLineInputStream() throws IOException {
        String body = "Line 1\nLine 2\nLine 3";
        ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(bais);
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, reader);
        assertEquals(200, response.getResponseCode());
        assertEquals("Line 1Line 2Line 3", response.getResponseBody());
    }

    @Test
    public void testIsResponse2xxWith200() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, "OK");
        assertTrue(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith201() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(201, "Created");
        assertTrue(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith299() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(299, "OK");
        assertTrue(response.isResponse2xx());
    }

    @Test
    public void testSetResponseCode() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, "OK");
        response.setResponseCode(404);
        assertEquals(404, response.getResponseCode());
    }

    // === Negative Tests ===

    @Test
    public void testIsResponse2xxWith400() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(400, "Bad Request");
        assertFalse(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith500() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(500, "Internal Server Error");
        assertFalse(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith100() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(100, "Continue");
        assertFalse(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith300() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(300, "Redirect");
        assertFalse(response.isResponse2xx());
    }

    @Test
    public void testIsResponse2xxWith199() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(199, "");
        assertFalse(response.isResponse2xx());
    }

    // === Boundary Tests ===

    @Test
    public void testResponseCodeZero() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(0, "");
        assertEquals(0, response.getResponseCode());
        assertFalse(response.isResponse2xx());
    }

    @Test
    public void testEmptyResponseBody() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, "");
        assertEquals("", response.getResponseBody());
        assertTrue(response.isResponse2xx());
    }

    @Test
    public void testEmptyInputStream() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        InputStreamReader reader = new InputStreamReader(bais);
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, reader);
        assertEquals("", response.getResponseBody());
    }

    @Test
    public void testLargeResponseBody() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("x");
        }
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(200, sb.toString());
        assertEquals(10000, response.getResponseBody().length());
    }

    @Test
    public void testNegativeResponseCode() {
        HttpClient.HttpResponse response = new HttpClient.HttpResponse(-1, "Error");
        assertEquals(-1, response.getResponseCode());
        assertFalse(response.isResponse2xx());
    }
}
