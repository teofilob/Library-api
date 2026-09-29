package br.com.teofilob.library.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class BookMapperTest {

    @Test
    void returnsNullWhenRequestIsNull() {
        assertNull(BookMapper.INSTANCE.toModel(null));
    }

    @Test
    void returnsNullWhenBookIsNull() {
        assertNull(BookMapper.INSTANCE.toDTO(null));
    }
}
