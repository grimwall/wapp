package com.aveng.wapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.wapp.domain.DiffEntity;
import com.aveng.wapp.service.dto.Diff;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface DiffMapper {

    DiffEntity map(Diff diff);

    Diff map(DiffEntity diffEntity);
}
