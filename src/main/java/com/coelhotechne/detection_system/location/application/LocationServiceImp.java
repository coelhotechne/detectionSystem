package com.coelhotechne.detection_system.location.application;

import com.coelhotechne.detection_system.location.api.dto.LocationMapper;
import com.coelhotechne.detection_system.location.api.dto.LocationRequest;
import com.coelhotechne.detection_system.location.api.dto.LocationResponse;
import com.coelhotechne.detection_system.location.domain.Location;
import com.coelhotechne.detection_system.location.exceptions.LocationNotFoundException;
import com.coelhotechne.detection_system.location.infrastructure.LocationRepository;
import com.coelhotechne.detection_system.zone.application.ZoneService;
import com.coelhotechne.detection_system.zone.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class LocationServiceImp implements LocationService{
    private final LocationRepository repository;
    private final LocationMapper mapper;
    private final ZoneService zoneService;

    @Override
    public List<LocationResponse> findLocationList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public LocationResponse findLocationId(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse).orElseThrow(()->{
            log.error("Id {} not found!",uuid);
            throw new LocationNotFoundException(uuid.toString(),null,null,"not found!");
        }) ;
    }

    @Override
    public LocationResponse createLocation(LocationRequest locationRequest) {
        Zone zone = zoneService.requireZone(locationRequest.zoneUuid());
        Location entity = mapper.toEntity(locationRequest);
        entity.setZone(zone);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public LocationResponse updateLocation(UUID uuid, LocationRequest locationRequest) {
        Location updated =repository.findById(uuid).orElseThrow(()->{
            log.error("Id {} not found!",uuid);
            throw new LocationNotFoundException(uuid.toString(),null,null,"not found!");
        }) ;
        Zone entityZone = zoneService.requireZone(locationRequest.zoneUuid());
        updated.setName(locationRequest.name());
        updated.setLatitude(locationRequest.latitude());
        updated.setLongitude(locationRequest.longitude());
        updated.setZone(entityZone);
        Location saved = repository.save(updated);
        return mapper.toResponse(saved);
    }

    @Override
    public LocationResponse deleteLocation(UUID uuid) {
        Location entity = repository.findById(uuid).orElseThrow(()->{
            log.error("Id {} not found!",uuid);
            throw new LocationNotFoundException(uuid.toString(),null,null,"not found!");
        }) ;
        repository.delete(entity);
        return mapper.toResponse(entity);
    }
}
