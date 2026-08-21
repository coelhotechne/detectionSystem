package com.coelhotechne.detection_system.globalClass.mapper;

import java.util.List;


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