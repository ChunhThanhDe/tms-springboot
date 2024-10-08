package com.vnptt.tms.converter;

import com.vnptt.tms.dto.DeviceDTO;
import com.vnptt.tms.entity.DeviceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceConverter {

    @Autowired
    private ModelMapper mapper;

    /**
     * Convert for method Post
     *
     * @param dto
     * @return
     */
    public DeviceEntity toEntity(DeviceDTO dto) {
        DeviceEntity entity = new DeviceEntity();
        entity = mapper.map(dto, DeviceEntity.class);
        return entity;
    }

    /**
     * Convert for mehtod get
     *
     * @param entity
     * @return
     */
    public DeviceDTO toDTO(DeviceEntity entity) {
        DeviceDTO dto = new DeviceDTO();
        if (entity.getId() != null) {
            dto.setId(entity.getId());
        }
        dto = mapper.map(entity, DeviceDTO.class);
        dto.setCreatedDate(entity.getCreatedDate());
//        dto.setCreatedBy(entity.getCreatedBy());
        dto.setModifiedDate(entity.getModifiedDate());
//        dto.setModifiedBy(entity.getModifiedBy());
        return dto;
    }

    /**
     * Convert for method put
     *
     * @param dto
     * @param entity
     * @return
     */
    public DeviceEntity toEntity(DeviceDTO dto, DeviceEntity entity) {
        if(dto.getDescription() != null){
            entity.setDescription(dto.getDescription());
        }
        if(dto.getHdmi() != null){
            entity.setHdmi(dto.getHdmi());
        }
        if(dto.getModel() != null){
            entity.setModel(dto.getModel());
        }
        if(dto.getFirmwareVer() != null){
            entity.setFirmwareVer(dto.getFirmwareVer());
        }
        if(dto.getLocation() != null){
            entity.setLocation(dto.getLocation());
        }
        if(dto.getProduct() != null){
            entity.setProduct(dto.getProduct());
        }
        if(dto.getRom() != null){
            entity.setRom(dto.getRom());
        }
        if(dto.getNetwork() != null){
            entity.setNetwork(dto.getNetwork());
        }
        if(dto.getMac() != null){
            entity.setMac(dto.getMac());
        }
//        unnecessary remove to reduce error
//        entity.setIp(dto.getIp());
//        entity.setDate(dto.getDate());
//        entity.setSn(dto.getSn());
        return entity;
    }
}
