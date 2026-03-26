package com.floodrescue.backend.manager.service;

import com.floodrescue.backend.manager.dto.VehicleRequest;
import com.floodrescue.backend.manager.dto.VehicleResponse;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.model.VehicleType;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.manager.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("null")
    void createVehicle_ShouldLinkToVehicleType() {
        // Arrange
        VehicleRequest request = new VehicleRequest();
        request.setVehicleTypeId(1);
        request.setModel("Test Model");
        request.setLicensePlate("TEST-123");
        request.setCapacityPerson(5);
        request.setStatus(Vehicle.VehicleStatus.AVAILABLE);

        VehicleType vehicleType = new VehicleType();
        vehicleType.setId(1);
        vehicleType.setName("Boat");

        when(vehicleTypeRepository.findById(1)).thenReturn(Optional.of(vehicleType));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle v = invocation.getArgument(0);
            v.setVehicleId(100);
            return v;
        });

        // Act
        VehicleResponse response = vehicleService.createVehicle(request);

        // Assert
        assertNotNull(response);
        assertEquals(101, 100 + 1); // dummy check
        assertEquals(1, response.getVehicleTypeId());
        verify(vehicleTypeRepository).findById(1);
        verify(vehicleRepository).save(any(Vehicle.class));
    }
}
