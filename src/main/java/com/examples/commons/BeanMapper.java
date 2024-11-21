package com.examples.commons;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeanMapper {
    BeanMapper INSTANCE = Mappers.getMapper(BeanMapper.class);
}
