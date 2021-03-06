package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlTestBase;

public class CaseInsensitiveDeserTest extends XmlTestBase
{
    // [databind#1036]
    static class BaseResponse {
        public int errorCode;
        public String debugMessage;
    }

    // [databind#1438]
    static class InsensitiveCreator
    {
        int v;

        @JsonCreator
        public InsensitiveCreator(@JsonProperty("value") int v0) {
            v = v0;
        }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    private final ObjectMapper MAPPER = newObjectMapper();

    private final ObjectMapper INSENSITIVE_MAPPER = newObjectMapper();
    {
        INSENSITIVE_MAPPER.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        
    }

    // [databind#1036]
    public void testCaseInsensitive1036() throws Exception
    {
        final String DOC =
"<BaseResponse><ErrorCode>2</ErrorCode><DebugMessage>Signature not valid!</DebugMessage></BaseResponse>";

        // Ok with insensitive
        BaseResponse response = INSENSITIVE_MAPPER.readValue(DOC, BaseResponse.class);
        assertEquals(2, response.errorCode);
        assertEquals("Signature not valid!", response.debugMessage);

        // but not without
        try {
            MAPPER.readValue(DOC, BaseResponse.class);
            fail("Should not pass");
        } catch (UnrecognizedPropertyException e) {
            verifyException(e, "ErrorCode");
        }
    }

    // [databind#1438]
    public void testCreatorWithInsensitive() throws Exception
    {
        final String DOC = aposToQuotes("<root><VALUE>3</VALUE></root>");
        InsensitiveCreator bean = INSENSITIVE_MAPPER.readValue(DOC, InsensitiveCreator.class);
        assertEquals(3, bean.v);
    }
}
