package com.coelhotechne.detection_system.zone.application;

import com.coelhotechne.detection_system.zone.api.dto.ZoneMapper;
import com.coelhotechne.detection_system.zone.api.dto.ZoneRequest;
import com.coelhotechne.detection_system.zone.api.dto.ZoneResponse;
import com.coelhotechne.detection_system.zone.domain.Zone;
import com.coelhotechne.detection_system.zone.exceptions.ZoneNotFoundException;
import com.coelhotechne.detection_system.zone.infrastructure.ZoneRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class ZoneServiceImp implements ZoneService{
    private final ZoneRepository repository;
    private final ZoneMapper mapper;
    @Override
    public List<ZoneResponse> findZoneList() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public ZoneResponse findZoneId(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponse).orElseThrow(()-> {
            log.error("Zone with id: {} not found!",uuid);
            throw new ZoneNotFoundException(uuid.toString(),"find one id","not found");
        });
    }

    @Override
    public ZoneResponse createZone(ZoneRequest zoneRequest) {
        Zone entity = mapper.toEntity(zoneRequest);
        entity.setCreatedZoneAtDay(LocalDate.now());
        Zone saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public ZoneResponse updateZone(UUID uuid, ZoneRequest zoneRequest) {
        Zone updated =  repository.findById(uuid).orElseThrow(()-> {
            log.error("Zone with id: {} not found!",uuid);
            throw new ZoneNotFoundException(uuid.toString(),"find one id","not found");
        });
        updated.setName(zoneRequest.name());
        updated.setDescription(zoneRequest.description());
        updated.setZoneType(zoneRequest.zoneType());
        updated.setCenterLatitude(zoneRequest.centerLatitude());
        updated.setCenterLongitude(zoneRequest.centerLongitude());
        updated.setRadiusMeters(zoneRequest.radiusMeters());
        updated.setActive(true);
        updated.setCreatedZoneAtDay(zoneRequest.createdZoneAtDay());
        Zone saved = repository.save(updated);
        return mapper.toResponse(saved);
    }

    @Override
    public ZoneResponse deleteZone(UUID uuid) {
        Zone deleted = repository.findById(uuid).orElseThrow(()-> {
            log.error("Zone with id: {} not found!",uuid);
            throw new ZoneNotFoundException(uuid.toString(),"find one id","not found");
        });
        repository.delete(deleted);
        return mapper.toResponse(deleted);
    }
}
