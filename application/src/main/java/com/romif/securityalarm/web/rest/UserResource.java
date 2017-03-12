package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.service.DeviceService;
import com.romif.securityalarm.service.dto.DeviceDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        String currentUserLogin = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if (!currentUserLogin.equals(login)) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<DeviceDTO> deviceDtos = deviceService.getAllActiveDevicesForUser(login);

        return new ResponseEntity<>(deviceDtos, HttpStatus.OK);
    }
}
