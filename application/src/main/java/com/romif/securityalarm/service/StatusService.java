package com.romif.securityalarm.service;

import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceState;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.repository.StatusRepository;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Service Implementation for managing Status.
 */
@Service
@Transactional
public class StatusService {

    private final Logger log = LoggerFactory.getLogger(StatusService.class);

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Save a status.
     *
     * @param status the entity to save
     * @return the persisted entity
     */
    @CachePut(value = "status", key = "#result.createdBy")
    @Transactional
    public Status save(Status status) {
        log.debug("Request to save Status : {}", status);
        Status result = statusRepository.save(status);
        if (DeviceState.CONFIGURED.equals(status.getDeviceState())) {
            deviceRepository.findOneByLogin(result.getCreatedBy())
                .ifPresent(device -> {
                    device.setConfigStatus(ConfigStatus.CONFIGURED);
                    deviceRepository.save(device);
                });
        }
        return result;
    }

    @CachePut(value = "statusQueue", key = "#status.createdBy")
    @Transactional
    public Queue<Status> putInQueue(Status status, Queue<Status> statusQueue) {
        statusQueue.add(status);
        return statusQueue;
    }

    /**
     *  Get all the statuses.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Status> findAll(Pageable pageable) {
        log.debug("Request to get all Statuses");
        Page<Status> result = statusRepository.findAll(pageable);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<Status> findAll(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate, String login) {
        log.debug("Request to get all Statuses");
        Page<Status> result;
        if (startDate != null && endDate != null) {
            result = statusRepository.findByCreatedDateAfterAndCreatedDateBeforeAndCreatedBy(startDate, endDate, login, pageable);
        } else {
            result = statusRepository.findByCreatedBy(login, pageable);
        }

        return result;
    }

    /**
     *  Get one status by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Status findOne(Long id) {
        log.debug("Request to get Status : {}", id);
        Status status = statusRepository.findOne(id);
        return status;
    }

    /**
     *  Delete the  status by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Status : {}", id);
        statusRepository.delete(id);
    }

    @Cacheable(value = "status", key = "#createdBy")
    @Transactional(readOnly = true)
    public Optional<Status> getLastStatusCreatedBy(String createdBy) {
        return statusRepository.findFirstByCreatedByOrderByCreatedDateDesc(createdBy);
    }

    @Cacheable(value = "statusQueue", key = "#createdBy")
    @Transactional(readOnly = true)
    public Queue<Status> getLast10StatusesCreatedBy(String createdBy) {
        Set<Status> statuses = statusRepository.findTop10ByCreatedByOrderByCreatedDateDesc(createdBy);
        Queue<Status> statusQueue = new CircularFifoQueue<>(10);
        statusQueue.addAll(statuses);
        return statusQueue;
    }

}
