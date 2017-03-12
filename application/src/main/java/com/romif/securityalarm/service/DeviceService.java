package com.romif.securityalarm.service;

import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.security.jwt.TokenProvider;
import com.romif.securityalarm.service.dto.DeviceConfigDTO;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import com.romif.securityalarm.service.mapper.DeviceMapper;
import com.romif.securityalarm.service.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private DeviceCredentialsRepository deviceCredentialsRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SmsTxtlocalService smsService;

    @Autowired
    private TokenProvider tokenProvider;

    public Optional<DeviceManagementDTO> getDevice(String login) {

        return deviceRepository.findOneByLogin(login)
            .map(device -> deviceMapper.deviceToDeviceManagementDTO(device));
    }

    public List<DeviceDTO> getAllDevicesForUser(String login) {

        return deviceRepository.findAllByUserLogin(login).stream()
            .map(device -> deviceMapper.deviceToDeviceDTO(device))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<DeviceDTO> getAllActiveDevicesForUser(String login) {

        return deviceRepository.findAllByUserLoginAndActiveTrue(login).stream()
            .map(device -> deviceMapper.deviceToDeviceDTO(device))
            .collect(Collectors.toList());
    }

    @Transactional
    public Page<DeviceDTO> getAllDevices(Pageable pageable) {

        return deviceRepository.findAll(pageable)
            .map(device -> deviceMapper.deviceToDeviceDTO(device));
    }

    @Transactional
    public Page<DeviceDTO> getAllFreeDevices(Pageable pageable) {

        return deviceRepository.findAllByUserIsNull(pageable)
            .map(device -> deviceMapper.deviceToDeviceDTO(device));
    }

    public Device createDevice(Device device) {

        String rawPassword = RandomUtil.generatePassword();
        String pauseToken = UUID.randomUUID().toString();
        String secret = RandomStringUtils.randomAlphanumeric(8);
        String login = RandomStringUtils.randomAlphanumeric(10);

        device.setPassword(passwordEncoder.encode(rawPassword));
        device.setPauseToken(passwordEncoder.encode(pauseToken));
        device.setLogin(login);

        String token = tokenProvider.createDeviceToken(device.getLogin());

        Device result = deviceRepository.save(device);

        DeviceCredentials deviceCredentials = new DeviceCredentials(null, result, token, pauseToken, secret);

        deviceCredentialsRepository.save(deviceCredentials);

        return result;
    }

    @Transactional
    public boolean updateDevice(DeviceDTO deviceDTO) {
        return deviceRepository.findOneById(deviceDTO.getId()).map(device -> {
            device.setPhone(deviceDTO.getPhone());
            device.setApn(deviceDTO.getApn());
            device.setDescription(deviceDTO.getDescription());
            deviceRepository.save(device);
            return true;
        }).orElse(false);
    }

    public void deleteDevice(String login) {
        deviceRepository.findOneByLogin(login).ifPresent(device -> {
            deviceRepository.delete(device);
            log.debug("Deleted Device: {}", device);
        });
    }

    public boolean activateDevice(String login) {
        return deviceRepository.findOneByLogin(login).map(device -> {
            device.setActive(true);
            deviceRepository.save(device);
            log.debug("Activated Device: {}", device);
            return true;
        }).orElse(false);
    }

    public CompletableFuture<ConfigStatus> configDevice(String login) {
        Optional<Device> device = deviceRepository.findOneByLogin(login);
        Optional<DeviceCredentials> deviceCredentials = deviceCredentialsRepository.findOneByDeviceLogin(login);
        if (device.isPresent() && deviceCredentials.isPresent()) {
            CompletableFuture<ConfigStatus> future = smsService.sendConfig(device.get(), deviceCredentials.get());
            future.thenApply(configStatus -> {
                device.get().setConfigStatus(configStatus);
                deviceRepository.save(device.get());
                return configStatus;
            });
            return future;
        } else {
            return CompletableFuture.completedFuture(ConfigStatus.NOT_CONFIGURED);
        }
    }

    public boolean deactivateDevice(String login) {
        return deviceRepository.findOneByLogin(login).map(device -> {
            device.setActive(false);
            deviceRepository.save(device);
            log.debug("Deactivated Device: {}", device);
            return true;
        }).orElse(false);

    }

    public Optional<DeviceConfigDTO> getConfig(String login) {
        return deviceRepository.findOneByLogin(login).map(device -> {
            DeviceConfigDTO deviceConfigDTO = new DeviceConfigDTO();
            deviceConfigDTO.setSendLocationPath(Constants.SEND_LOCATION_PATH);
            deviceConfigDTO.setPauseAlarmPath(Constants.PAUSE_ALARM_PATH);
            deviceConfigDTO.setResumeAlarmPath(Constants.RESUME_ALARM_PATH);
            deviceConfigDTO.setPhone(StringUtils.remove(device.getUser().getPhone(), "+"));
            return deviceConfigDTO;
        });
    }
}
