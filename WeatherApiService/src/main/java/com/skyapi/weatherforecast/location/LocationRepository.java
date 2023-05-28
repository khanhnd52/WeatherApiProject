package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, String> {

    @Query("select l from Location l where l.trashed = false ")
    public List<Location> findUntrashed();
}
