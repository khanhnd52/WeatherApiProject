package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTests {
    @Autowired
    private LocationRepository repository;

    @Test
    public void testAddSuccess() {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");
        location.setEnabled(true);

        Location savedLocation = repository.save(location);

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
    }

    @Test
    public void testListSuccess() {
        List<Location> locations = repository.findUntrashed();

        assertThat(locations).isNotEmpty();

        locations.forEach(System.out::println);
    }
}
