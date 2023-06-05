package com.vnptt.tms.service.impl;

import com.vnptt.tms.converter.ListDeviceConverter;
import com.vnptt.tms.dto.ListDeviceDTO;
import com.vnptt.tms.entity.ListDeviceEntity;
import com.vnptt.tms.entity.RoleManagementEntity;
import com.vnptt.tms.exception.ResourceNotFoundException;
import com.vnptt.tms.repository.DeviceRepository;
import com.vnptt.tms.repository.ListDeviceRepository;
import com.vnptt.tms.repository.RoleManagementRepository;
import com.vnptt.tms.repository.UserRepository;
import com.vnptt.tms.service.IListDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class ListDeviceService implements IListDeviceService {

    @Autowired
    private ListDeviceRepository listDeviceRepository;

    @Autowired
    private RoleManagementRepository roleManagementRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ListDeviceConverter listDeviceConverter;


    /**
     * unnecessary (only use to test)
     * save to database when post and put app
     *
     * @return
     */
    @Override
    public ListDeviceDTO save(ListDeviceDTO listDeviceDTO) {
        ListDeviceEntity listDeviceEntity = new ListDeviceEntity();
        if (listDeviceDTO.getId() != null) {
            Optional<ListDeviceEntity> oldListDeviceEntity = listDeviceRepository.findById(listDeviceDTO.getId());
            listDeviceEntity = listDeviceConverter.toEntity(listDeviceDTO, oldListDeviceEntity.get());
        } else {
            listDeviceEntity = listDeviceConverter.toEntity(listDeviceDTO);
        }
        listDeviceEntity = listDeviceRepository.save(listDeviceEntity);
        return listDeviceConverter.toDTO(listDeviceEntity);
    }

    /**
     * find app with id
     *
     * @param id
     * @return
     */
    @Override
    public ListDeviceDTO findOne(Long id) {
        ListDeviceEntity entity = listDeviceRepository.findOneById(id);
        return listDeviceConverter.toDTO(entity);
    }

    /**
     * total item app on database
     *
     * @return
     */
    @Override
    public int totalItem() {
        return (int) listDeviceRepository.count();
    }

    /**
     * Delete app on database
     *
     * @param ids list id app
     */
    @Override
    public void delete(Long[] ids) {
        for (Long item : ids) {
            listDeviceRepository.deleteById(item);
        }
    }

    /**
     * find all app with pageable
     *
     * @param pageable
     * @return
     */
    @Override
    public List<ListDeviceDTO> findAll(Pageable pageable) {
        List<ListDeviceEntity> entities = listDeviceRepository.findAll(pageable).getContent();
        List<ListDeviceDTO> result = new ArrayList<>();
        for (ListDeviceEntity item : entities) {
            ListDeviceDTO ListDeviceDTO = listDeviceConverter.toDTO(item);
            result.add(ListDeviceDTO);
        }
        return result;
    }

    /**
     * find all app nomal
     *
     * @return
     */
    @Override
    public List<ListDeviceDTO> findAll() {
        List<ListDeviceEntity> entities = listDeviceRepository.findAll();
        List<ListDeviceDTO> result = new ArrayList<>();
        for (ListDeviceEntity item : entities) {
            ListDeviceDTO ListDeviceDTO = listDeviceConverter.toDTO(item);
            result.add(ListDeviceDTO);
        }
        return result;
    }

    /**
     * Add listDevice to Role Management
     *
     * @param roleManagementId
     * @param listDeviceId
     * @return
     */
    @Override
    public ListDeviceDTO addListDeviceToRolesManagement(Long roleManagementId, Long listDeviceId) {
        ListDeviceEntity listDeviceEntity = roleManagementRepository.findById(roleManagementId).map(roleManagement -> {
            ListDeviceEntity listDevice = listDeviceRepository.findById(listDeviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found list device with id = " + listDeviceId));

            // check if device has still
            List<ListDeviceEntity> deviceEntities = roleManagement.getDeviceEntities();
            for (ListDeviceEntity item : deviceEntities) {
                if (item.equals(listDevice)) {
                    return listDevice;
                }
            }
            //map and add device to policy
            roleManagement.addListDevice(listDevice);
            roleManagementRepository.save(roleManagement);
            return listDevice;
        }).orElseThrow(() -> new ResourceNotFoundException("Not found roles management with id = " + roleManagementId));
        return listDeviceConverter.toDTO(listDeviceEntity);
    }

    @Override
    public void removeListDeviceInManagement(Long roleManagementId, Long listDeviceId) {
        RoleManagementEntity roleManagementEntity = roleManagementRepository.findById(roleManagementId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found roles Management with id = " + roleManagementId));

        List<ListDeviceEntity> entities = roleManagementEntity.getDeviceEntities();
        boolean remove = false;
        for (ListDeviceEntity entity : entities) {
            if (Objects.equals(entity.getId(), listDeviceId)) {
                remove = true;
            }
        }
        if (remove) {
            roleManagementEntity.removeListDevice(listDeviceId);
            roleManagementRepository.save(roleManagementEntity);
        } else {
            throw new ResourceNotFoundException("policy don't have List Device with id = " + listDeviceId);
        }
    }

    /**
     *
     * @param roleManagementId
     * @return
     */
    @Override
    public List<ListDeviceDTO> findListDeviceInRoleManagement(Long roleManagementId) {
        List<ListDeviceDTO> result = new ArrayList<>();
        RoleManagementEntity roleManagement = roleManagementRepository.findOneById(roleManagementId);
        if (roleManagement == null) {
            throw new ResourceNotFoundException("not found list device with Id = " + roleManagementId);
        }
        List<ListDeviceEntity> deviceEntities = roleManagement.getDeviceEntities();
        for (ListDeviceEntity entity : deviceEntities) {
            result.add(listDeviceConverter.toDTO(entity));
        }
        return result;
    }

}