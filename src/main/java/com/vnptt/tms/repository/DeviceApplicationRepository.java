package com.vnptt.tms.repository;

import com.vnptt.tms.entity.DeviceApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface DeviceApplicationRepository extends JpaRepository<DeviceApplicationEntity, Long> {

    DeviceApplicationEntity findOneById(Long id);

    DeviceApplicationEntity findDeviceApplicationEntityByDeviceAppEntityDetailIdAndApplicationEntityDetailId(Long deviceId, Long applicationId);

    DeviceApplicationEntity findDeviceApplicationEntityByDeviceAppEntityDetailSnAndApplicationEntityDetailId(String sn, Long applicationId);

    List<DeviceApplicationEntity> findByDeviceAppEntityDetailIdOrderByModifiedDateDesc(Long deviceId, Pageable pageable);

    List<DeviceApplicationEntity> findByDeviceAppEntityDetailIdAndIsaliveAndApplicationEntityDetailNameContainingOrderByModifiedDateDesc(Long deviceId, boolean isAlive, String name, Pageable pageable);

    Long countByDeviceAppEntityDetailId(Long deviceId);

    Long countByDeviceAppEntityDetailIdAndIsaliveAndApplicationEntityDetailNameContainingAndApplicationEntityDetailIssystem(Long deviceId, boolean isAlive, String name, Boolean isSystem);

    List<DeviceApplicationEntity> findAllByApplicationEntityDetailIdOrderByModifiedDateDesc(Long applicationId, Pageable pageable);

    List<DeviceApplicationEntity> findAllByApplicationEntityDetailIdAndDeviceAppEntityDetailSnContainingOrderByModifiedDateDesc(Long applicationId,String sn, Pageable pageable);

    Long countAllByApplicationEntityDetailIdAndDeviceAppEntityDetailSnContaining(Long applicationId, String sn);

    Long countAllByApplicationEntityDetailId(Long applicationId);


    List<DeviceApplicationEntity> findAllByApplicationEntityDetailNameOrderByModifiedDateDesc(String name, Pageable pageable);

    Long countByApplicationEntityDetailNameContaining(String name);

    DeviceApplicationEntity findOneByDeviceAppEntityDetailSnAndApplicationEntityDetailPackagenameAndApplicationEntityDetailVersionAndIsalive(String sn, String packagename, Long version, Boolean isalive);

    Long countByApplicationEntityDetailId(Long applicationId);

    Long countByApplicationEntityDetailIdAndHistoryApplicationEntitiesDetailCreatedDateBetweenAndHistoryApplicationEntitiesDetailMain(Long applicationId, @Param("localDateTime") LocalDateTime localDateTime, @Param("localDateTimeNow") LocalDateTime localDateTimeNow, boolean main);

    Long countByApplicationEntityDetailIdAndHistoryApplicationEntitiesDetailCreatedDateBetween(Long applicationId, @Param("localDateTime") LocalDateTime localDateTime, @Param("localDateTimeNow") LocalDateTime localDateTimeNow);

    DeviceApplicationEntity findOneByDeviceAppEntityDetailIdAndApplicationEntityDetailPackagename(Long deviceId, String packagename);

}
