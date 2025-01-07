package dev.quochung2003.reactiveLearning.mapper;

import dev.quochung2003.reactiveLearning.dto.request.RegisterRequest;
import dev.quochung2003.reactiveLearning.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isCredentialsExpired", ignore = true)
    @Mapping(target = "isAccountLocked", ignore = true)
    @Mapping(target = "isAccountExpired", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authenticable", ignore = true)
    User requestToModel(RegisterRequest request);
}
