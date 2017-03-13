package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.security.SecurityUtils;
import com.romif.securityalarm.service.DeviceService;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/devices")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<DeviceDTO>> getDevicesForUser(@PathVariable String login) {
        log.debug("REST request to get Devices for User : {}", login);

        List<DeviceDTO> deviceDtos;

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            deviceDtos = deviceService.getAllDevicesForUser(login);
        } else {

            if (!SecurityUtils.getCurrentUserLogin().equals(login)) {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            deviceDtos = deviceService.getAllActiveDevicesForUser(login);
        }

        return new ResponseEntity<>(deviceDtos, HttpStatus.OK);
    }

    @PostMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/devices")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<DeviceDTO>> addDeviceForUser(@PathVariable String login, @RequestBody DeviceDTO deviceDTO) {
        log.debug("REST request to get Devices for User : {}", login);

        if(deviceService.addDeviceToUser(login, deviceDTO)) {
            return ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is added to User " + login, login)).build();
        } else {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error while adding device to User "  + login))
                .body(null);
        }

    }
}
