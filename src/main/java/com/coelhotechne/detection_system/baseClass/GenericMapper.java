package com.coelhotechne.detection_system.baseClass;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GenericMapper <E,R,Q>{
    //E = Entidade
    //R= Response
    //Q= Request
    E toEntity(Q request);
    R toResponse(E entity);

    default List<R> toResponseList(List<E>entities){
        return entities.stream().map(this::toResponse).toList();
    }

}