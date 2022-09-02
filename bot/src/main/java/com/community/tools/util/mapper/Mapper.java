package com.community.tools.util.mapper;

public interface Mapper<E, D> {

  D entityToDto(E entity);

  D dtoToEntity(E entity);

}
