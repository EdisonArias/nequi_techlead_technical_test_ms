package co.com.nequi.teachlead.technical.test.api.shared.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void buildWithDataShouldSetOkAndSuccessAndData() {
        // Arrange
        String payload = "hello";

        // Act
        Response<String> resp = Response.build(payload);

        // Assert
        assertEquals("200", resp.getCode());
        assertEquals("SUCCESS", resp.getMessage());
        assertEquals(payload, resp.getData());
    }

    @Test
    void buildWithNullDataShouldStillReturnOkAndSuccess() {
        // Arrange
        Object payload = null;

        // Act
        Response<Object> resp = Response.build(payload);

        // Assert
        assertEquals("200", resp.getCode());
        assertEquals("SUCCESS", resp.getMessage());
        assertNull(resp.getData());
    }

    @Test
    void buildCustomCodeMessageDataShouldSetAllFields() {
        // Arrange
        String code = "404";
        String message = "Not Found";
        Integer data = 123;

        // Act
        Response<Integer> resp = Response.build(code, message, data);

        // Assert
        assertEquals(code, resp.getCode());
        assertEquals(message, resp.getMessage());
        assertEquals(data, resp.getData());
    }

    @Test
    void buildGenericListDataShouldPreserveGeneric() {
        // Arrange
        List<String> data = List.of("a", "b", "c");

        // Act
        Response<List<String>> resp = Response.build(data);

        // Assert
        assertEquals("200", resp.getCode());
        assertEquals("SUCCESS", resp.getMessage());
        assertNotNull(resp.getData());
        assertEquals(3, resp.getData().size());
        assertIterableEquals(List.of("a", "b", "c"), resp.getData());
    }
}
