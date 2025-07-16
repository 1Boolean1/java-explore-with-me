package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.LocationDto;
import ru.practicum.explorewithme.main.models.Location;

public class LocationMapper {
    public static LocationDto toDto(Location location) {
        return new LocationDto(location.getLat(),
                location.getLon());
    }
}
