package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AlarmMapper.class)
public interface DeviceMapper {

    DeviceDTO deviceManagementDTOToDeviceDTO(DeviceManagementDTO deviceManagementDTO);

    @Mapping(target = "name", source = "login")
    @Mapping(target = "token", source = "deviceCredentials.token")
    @Mapping(target = "pauseToken", source = "deviceCredentials.pauseToken")
    @Mapping(target = "secret", source = "deviceCredentials.secret")
    DeviceManagementDTO deviceToDeviceManagementDTO(Device device);

    @Mapping(target = "name", source = "login")
    @Mapping(target = "userLogin", source = "user.login")
    DeviceDTO deviceToDeviceDTO(Device device);

}
