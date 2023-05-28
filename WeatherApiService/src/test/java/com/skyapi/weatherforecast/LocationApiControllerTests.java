package com.skyapi.weatherforecast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationApiController;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {
    private static String END_POINT_PATH = "/v1/locations";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean //Create fake LocationService vi WebMvcTest khong goi toi Service
    LocationService service;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location();

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testShouldReturn201Created() throws Exception {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Mockito.when(service.add(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYC_USA"))
                .andDo(print());
    }

    @Test
    public void testListShouldReturn204NoContent() throws Exception {
        Mockito.when(service.list()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testListShouldReturn200OK() throws Exception {
        Location location1 = new Location();
        location1.setCode("NYC_USA");
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryName("United States of America");
        location1.setCountryCode("US");
        location1.setEnabled(true);

        Location location2 = new Location();
        location2.setCode("LACA_USA");
        location2.setCityName("Los Angeles");
        location2.setRegionName("California");
        location2.setCountryName("United States of America");
        location2.setCountryCode("US");
        location2.setEnabled(true);

        Mockito.when(service.list()).thenReturn(List.of(location1, location2));

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].code", is("NYC_USA")))
                .andExpect(jsonPath("$[0].city_name", is("New York City")))
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn405MethodNotAllowed() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDE";

        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDE";

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn200OK() throws Exception {
        String code = "LACA_USA";
        String requestURI = END_POINT_PATH + "/" + code;

        Location location = new Location();
        location.setCode("LACA_USA");
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Mockito.when(service.get(code)).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(code)))
                .andExpect(jsonPath("$.city_name", is("Los Angeles")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws LocationNotFoundException, Exception {
        Location location = new Location();
        location.setCode("LACA_USA");
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));

        String bodyContent = mapper.writeValueAsString(location);
        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        Location location = new Location();
        location.setCityName("Los Angeles");
        location.setRegionName("California");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception, LocationNotFoundException {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);


        Mockito.when(service.update(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());
    }

}
