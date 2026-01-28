package com.floodrescue.backend.common.config;

import com.floodrescue.backend.auth.model.Role;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.RoleRepository;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.model.Warehouse;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.manager.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      RequestRepository requestRepository,
                      VehicleRepository vehicleRepository,
                      WarehouseRepository warehouseRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedRequests();
        seedVehicles();
        seedWarehouses();
    }

    private void seedRoles() {
        String[] roleNames = {"ADMIN", "CITIZEN", "RESCUE_TEAM", "RESCUE_COORDINATOR"};
        for (String roleName : roleNames) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
        }
    }

    private void seedUsers() {
        Map<String, Role> roleMap = new HashMap<>();
        roleRepository.findAll().forEach(role -> roleMap.put(role.getName(), role));

        // 1. Admin
        if (!userRepository.existsByEmail("admin@floodrescue.com")) {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@floodrescue.com");
            admin.setPhoneNumber("0900000001");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(roleMap.get("ADMIN"));
            admin.setIsActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
        }

        // 2. Citizen
        if (!userRepository.existsByEmail("citizen@test.com")) {
            User citizen = new User();
            citizen.setFullName("Test Citizen");
            citizen.setEmail("citizen@test.com");
            citizen.setPhoneNumber("0900000002");
            citizen.setPasswordHash(passwordEncoder.encode("citizen123"));
            citizen.setRole(roleMap.get("CITIZEN"));
            citizen.setIsActive(true);
            citizen.setCreatedAt(LocalDateTime.now());
            userRepository.save(citizen);
        }

        // 3. Rescue Team
        if (!userRepository.existsByEmail("team@rescue.com")) {
            User team = new User();
            team.setFullName("Rescue Team");
            team.setEmail("team@rescue.com");
            team.setPhoneNumber("0900000003");
            team.setPasswordHash(passwordEncoder.encode("team123"));
            team.setRole(roleMap.get("RESCUE_TEAM"));
            team.setIsActive(true);
            team.setCreatedAt(LocalDateTime.now());
            userRepository.save(team);
        }
    }

    private void seedRequests() {
        if (requestRepository.count() > 0) {
            return;
        }

        User citizen = userRepository.findByEmail("citizen@test.com")
                .orElseGet(() -> {
                    Role citizenRole = roleRepository.findByName("CITIZEN")
                            .orElseGet(() -> roleRepository.save(new Role(null, "CITIZEN")));
                    User newCitizen = new User();
                    newCitizen.setFullName("Test Citizen");
                    newCitizen.setEmail("citizen@test.com");
                    newCitizen.setPhoneNumber("0900000002");
                    newCitizen.setPasswordHash(passwordEncoder.encode("citizen123"));
                    newCitizen.setRole(citizenRole);
                    newCitizen.setIsActive(true);
                    newCitizen.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newCitizen);
                });

        // Request 1 - CREATED
        Request request1 = new Request();
        request1.setUser(citizen);
        request1.setPhone(citizen.getPhoneNumber());
        request1.setRequestType(Request.RequestType.RESCUE);
        request1.setLatitude(new BigDecimal("10.762622"));
        request1.setLongitude(new BigDecimal("106.660172"));
        request1.setDescription("Family of 4 trapped on rooftop, water level rising.");
        request1.setPriority(Request.Priority.CRITICAL);
        request1.setStatus(Request.RequestStatus.CREATED);
        request1.setRequestSupplies("Life jackets, rope");
        request1.setRequestMedia("https://example.com/media1.jpg");
        request1.setCreatedAt(LocalDateTime.now().minusHours(1));
        requestRepository.save(request1);

        // Request 2 - IN_PROGRESS
        Request request2 = new Request();
        request2.setUser(citizen);
        request2.setPhone(citizen.getPhoneNumber());
        request2.setRequestType(Request.RequestType.RESCUE);
        request2.setLatitude(new BigDecimal("10.776889"));
        request2.setLongitude(new BigDecimal("106.700806"));
        request2.setDescription("Elderly couple stuck in flooded house, need evacuation.");
        request2.setPriority(Request.Priority.HIGH);
        request2.setStatus(Request.RequestStatus.IN_PROGRESS);
        request2.setRequestSupplies("Boat, medical kit");
        request2.setRequestMedia("No media provided");
        request2.setCreatedAt(LocalDateTime.now().minusMinutes(45));
        requestRepository.save(request2);

        // Request 3 - CREATED (relief)
        Request request3 = new Request();
        request3.setUser(citizen);
        request3.setPhone(citizen.getPhoneNumber());
        request3.setRequestType(Request.RequestType.RELIEF);
        request3.setLatitude(new BigDecimal("10.823099"));
        request3.setLongitude(new BigDecimal("106.629662"));
        request3.setDescription("Community center needs food and water supplies.");
        request3.setPriority(Request.Priority.NORMAL);
        request3.setStatus(Request.RequestStatus.CREATED);
        request3.setRequestSupplies("Bottled water, instant noodles");
        request3.setRequestMedia("No media provided");
        request3.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        requestRepository.save(request3);
    }

    private void seedVehicles() {
        if (vehicleRepository.count() > 0) {
            return;
        }

        // For now, we create vehicles without linking to a specific depot (null depot is allowed)

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setDepot(null);
        vehicle1.setType("Boat");
        vehicle1.setModel("Rescue Boat 3000");
        vehicle1.setLicensePlate("VR-001");
        vehicle1.setCapacityPerson(8);
        vehicle1.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setDepot(null);
        vehicle2.setType("Truck");
        vehicle2.setModel("Cargo Truck X1");
        vehicle2.setLicensePlate("VT-002");
        vehicle2.setCapacityPerson(3);
        vehicle2.setStatus(Vehicle.VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle2);

        Vehicle vehicle3 = new Vehicle();
        vehicle3.setDepot(null);
        vehicle3.setType("Ambulance");
        vehicle3.setModel("Ambulance A1");
        vehicle3.setLicensePlate("VA-003");
        vehicle3.setCapacityPerson(4);
        vehicle3.setStatus(Vehicle.VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle3);
    }

    private void seedWarehouses() {
        if (warehouseRepository.count() > 0) {
            return;
        }

        User admin = userRepository.findByEmail("admin@floodrescue.com")
                .orElseThrow(() -> new IllegalStateException("Admin user not found. Ensure users are seeded before warehouses."));

        Warehouse warehouse1 = new Warehouse();
        warehouse1.setUser(admin);
        warehouse1.setResourceId("WH-001");
        warehouse1.setSupplyId("SUP-FOOD-001");
        warehouse1.setStatus("ACTIVE");
        warehouseRepository.save(warehouse1);

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setUser(admin);
        warehouse2.setResourceId("WH-002");
        warehouse2.setSupplyId("SUP-MED-001");
        warehouse2.setStatus("ACTIVE");
        warehouseRepository.save(warehouse2);

        Warehouse warehouse3 = new Warehouse();
        warehouse3.setUser(admin);
        warehouse3.setResourceId("WH-003");
        warehouse3.setSupplyId("SUP-WATER-001");
        warehouse3.setStatus("INACTIVE");
        warehouseRepository.save(warehouse3);
    }
}

