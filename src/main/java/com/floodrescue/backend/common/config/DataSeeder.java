package com.floodrescue.backend.common.config;

import com.floodrescue.backend.auth.model.Role;
import com.floodrescue.backend.auth.model.User;
import com.floodrescue.backend.auth.repository.RoleRepository;
import com.floodrescue.backend.auth.repository.UserRepository;
import com.floodrescue.backend.citizen.model.Request;
import com.floodrescue.backend.citizen.repository.RequestRepository;
import com.floodrescue.backend.manager.model.Inventory;
import com.floodrescue.backend.manager.model.Item;
import com.floodrescue.backend.manager.model.ReliefDistribution;
import com.floodrescue.backend.manager.model.Vehicle;
import com.floodrescue.backend.manager.model.Warehouse;
import com.floodrescue.backend.manager.repository.InventoryRepository;
import com.floodrescue.backend.manager.repository.ItemRepository;
import com.floodrescue.backend.manager.repository.ReliefDistributionRepository;
import com.floodrescue.backend.manager.repository.VehicleRepository;
import com.floodrescue.backend.manager.repository.WarehouseRepository;
import com.floodrescue.backend.rescue.model.Mission;
import com.floodrescue.backend.rescue.model.RescueTeam;
import com.floodrescue.backend.rescue.model.Report;
import com.floodrescue.backend.rescue.model.TeamMember;
import com.floodrescue.backend.rescue.repository.MissionRepository;
import com.floodrescue.backend.rescue.repository.RescueTeamRepository;
import com.floodrescue.backend.rescue.repository.ReportRepository;
import com.floodrescue.backend.rescue.repository.TeamMemberRepository;
import com.floodrescue.backend.rescue.model.TeamPosition;
import com.floodrescue.backend.rescue.repository.TeamPositionRepository;
import com.floodrescue.backend.admin.repository.NotificationRepository;
import com.floodrescue.backend.admin.model.Notification;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Transactional
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = false)
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final ReliefDistributionRepository reliefDistributionRepository;
    private final MissionRepository missionRepository;
    private final ReportRepository reportRepository;
    private final RescueTeamRepository rescueTeamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamPositionRepository teamPositionRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
            UserRepository userRepository,
            RequestRepository requestRepository,
            VehicleRepository vehicleRepository,
            WarehouseRepository warehouseRepository,
            InventoryRepository inventoryRepository,
            ItemRepository itemRepository,
            ReliefDistributionRepository reliefDistributionRepository,
            MissionRepository missionRepository,
            ReportRepository reportRepository,
            RescueTeamRepository rescueTeamRepository,
            TeamMemberRepository teamMemberRepository,
            TeamPositionRepository teamPositionRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
        this.reliefDistributionRepository = reliefDistributionRepository;
        this.missionRepository = missionRepository;
        this.reportRepository = reportRepository;
        this.rescueTeamRepository = rescueTeamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamPositionRepository = teamPositionRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================================
    // HCM City District Data: { name, latitude, longitude, address, slug }
    // 22 districts with real GPS center-point coordinates
    // =====================================================================
    private static final String[][] HCM_DISTRICTS = {
            { "Quận 1", "10.776889", "106.700806",
                    "Kho Cứu trợ Quận 1, Đường Lê Duẩn, Phường Bến Nghé, Quận 1, TP.HCM", "quan1" },
            { "Quận 3", "10.784680", "106.686930",
                    "Kho Cứu trợ Quận 3, Đường Võ Văn Tần, Phường 5, Quận 3, TP.HCM", "quan3" },
            { "Quận 4", "10.757640", "106.706370",
                    "Kho Cứu trợ Quận 4, Đường Bến Vân Đồn, Phường 1, Quận 4, TP.HCM", "quan4" },
            { "Quận 5", "10.754120", "106.662360",
                    "Kho Cứu trợ Quận 5, Đường An Dương Vương, Phường 3, Quận 5, TP.HCM", "quan5" },
            { "Quận 6", "10.748220", "106.635460",
                    "Kho Cứu trợ Quận 6, Đường Hậu Giang, Phường 4, Quận 6, TP.HCM", "quan6" },
            { "Quận 7", "10.734060", "106.721620",
                    "Kho Cứu trợ Quận 7, Đường Nguyễn Thị Thập, Phường Tân Phú, Quận 7, TP.HCM", "quan7" },
            { "Quận 8", "10.740150", "106.652280",
                    "Kho Cứu trợ Quận 8, Đường Phạm Thế Hiển, Phường 4, Quận 8, TP.HCM", "quan8" },
            { "Quận 10", "10.774160", "106.667890",
                    "Kho Cứu trợ Quận 10, Đường 3 Tháng 2, Phường 12, Quận 10, TP.HCM", "quan10" },
            { "Quận 11", "10.764830", "106.650150",
                    "Kho Cứu trợ Quận 11, Đường Lạc Long Quân, Phường 3, Quận 11, TP.HCM", "quan11" },
            { "Quận 12", "10.867230", "106.641690",
                    "Kho Cứu trợ Quận 12, Đường Nguyễn Ảnh Thủ, Phường Hiệp Thành, Quận 12, TP.HCM", "quan12" },
            { "Quận Bình Thạnh", "10.810560", "106.709270",
                    "Kho Cứu trợ Bình Thạnh, Đường Xô Viết Nghệ Tĩnh, Phường 21, Quận Bình Thạnh, TP.HCM",
                    "binhthanh" },
            { "Quận Gò Vấp", "10.838500", "106.651820",
                    "Kho Cứu trợ Gò Vấp, Đường Quang Trung, Phường 10, Quận Gò Vấp, TP.HCM", "govap" },
            { "Quận Phú Nhuận", "10.798490", "106.680380",
                    "Kho Cứu trợ Phú Nhuận, Đường Phan Đình Phùng, Phường 1, Quận Phú Nhuận, TP.HCM",
                    "phunhuan" },
            { "Quận Tân Bình", "10.801750", "106.652620",
                    "Kho Cứu trợ Tân Bình, Đường Cộng Hòa, Phường 13, Quận Tân Bình, TP.HCM", "tanbinh" },
            { "Quận Tân Phú", "10.790780", "106.628520",
                    "Kho Cứu trợ Tân Phú, Đường Lũy Bán Bích, Phường Hòa Thạnh, Quận Tân Phú, TP.HCM",
                    "tanphu" },
            { "Quận Bình Tân", "10.765630", "106.604260",
                    "Kho Cứu trợ Bình Tân, Đường Kinh Dương Vương, Phường An Lạc, Quận Bình Tân, TP.HCM",
                    "binhtan" },
            { "TP. Thủ Đức", "10.851590", "106.753860",
                    "Kho Cứu trợ Thủ Đức, Đường Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức, TP.HCM",
                    "thuduc" },
            { "Huyện Nhà Bè", "10.694740", "106.734280",
                    "Kho Cứu trợ Nhà Bè, Đường Lê Văn Lương, TT. Nhà Bè, Huyện Nhà Bè, TP.HCM", "nhabe" },
            { "Huyện Hóc Môn", "10.886010", "106.593210",
                    "Kho Cứu trợ Hóc Môn, Đường Lý Thường Kiệt, TT. Hóc Môn, Huyện Hóc Môn, TP.HCM",
                    "hocmon" },
            { "Huyện Củ Chi", "10.973160", "106.493200",
                    "Kho Cứu trợ Củ Chi, Đường Tỉnh Lộ 8, TT. Củ Chi, Huyện Củ Chi, TP.HCM", "cuchi" },
            { "Huyện Cần Giờ", "10.411540", "106.953710",
                    "Kho Cứu trợ Cần Giờ, Đường Rừng Sác, TT. Cần Thạnh, Huyện Cần Giờ, TP.HCM", "cangio" },
            { "Huyện Bình Chánh", "10.716020", "106.594130",
                    "Kho Cứu trợ Bình Chánh, Đường Quốc Lộ 1A, TT. Tân Túc, Huyện Bình Chánh, TP.HCM",
                    "binhchanh" },
    };

    // 5 strategic districts that will have rescue teams (Center, East, West, South,
    // North)
    private static final Set<String> STRATEGIC_DISTRICTS = Set.of(
            "quan1", // Center
            "thuduc", // East
            "binhtan", // West
            "quan7", // South
            "quan12" // North
    );

    private static final int TEAMS_PER_STRATEGIC_DISTRICT = 2;
    private static final int MEMBERS_PER_TEAM = 7;

    @Override
    public void run(String... args) {
        // Safe seeding: No more TRUNCATE CASCADE to avoid data loss.
        seedRoles();
        seedUsers();
        seedRequests();
        seedVehicles();
        seedHcmData();
        seedMissions();
        seedInventoryAndReliefDistribution();
        seedMissionReport();
        seedNotifications();
    }

    private void seedRoles() {
        String[] roleNames = { "ADMIN", "CITIZEN", "RESCUE_TEAM", "RESCUE_COORDINATOR", "COORDINATOR", "MANAGER" };
        for (String roleName : roleNames) {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(null, roleName));
            }
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

        // 2. Manager
        if (!userRepository.existsByEmail("manager@test.com")) {
            User manager = new User();
            manager.setFullName("Test Manager");
            manager.setEmail("manager@test.com");
            manager.setPhoneNumber("0900000004");
            manager.setPasswordHash(passwordEncoder.encode("manager123"));
            manager.setRole(roleMap.get("MANAGER"));
            manager.setIsActive(true);
            manager.setCreatedAt(LocalDateTime.now());
            userRepository.save(manager);
        }

        // 3. Coordinator
        if (!userRepository.existsByEmail("coordinator@test.com")) {
            User coordinator = new User();
            coordinator.setFullName("Test Coordinator");
            coordinator.setEmail("coordinator@test.com");
            coordinator.setPhoneNumber("0900000005");
            coordinator.setPasswordHash(passwordEncoder.encode("coordinator123"));
            coordinator.setRole(roleMap.get("COORDINATOR"));
            coordinator.setIsActive(true);
            coordinator.setCreatedAt(LocalDateTime.now());
            userRepository.save(coordinator);
        }

        // 4. Citizen
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

        // 5. Rescue Team (legacy single user — kept for backward compatibility)
    }

    private void seedNotifications() {
        if (notificationRepository.count() > 0) {
            return;
        }

        userRepository.findByEmail("admin@floodrescue.com").ifPresent(admin -> {
            Notification n1 = new Notification();
            n1.setUser(admin);
            n1.setMessage("Chào mừng bạn quay lại hệ thống điều hành cứu hộ.");
            n1.setIsRead(false);
            n1.setCreatedAt(LocalDateTime.now().minusMinutes(5));
            notificationRepository.save(n1);
        });

        userRepository.findByEmail("citizen@test.com").ifPresent(citizen -> {
            Notification n2 = new Notification();
            n2.setUser(citizen);
            n2.setMessage("Yêu cầu cứu hộ của bạn đã được tiếp nhận.");
            n2.setIsRead(false);
            n2.setCreatedAt(LocalDateTime.now().minusMinutes(10));
            notificationRepository.save(n2);
        });
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

        // Request 1 - PENDING (rescue)
        Request request1 = new Request();
        request1.setUser(citizen);
        request1.setPhone(citizen.getPhoneNumber());
        request1.setRequestType(Request.RequestType.RESCUE);
        request1.setLatitude(new BigDecimal("10.762622"));
        request1.setLongitude(new BigDecimal("106.660172"));
        request1.setDescription("Family of 4 trapped on rooftop, water level rising.");
        request1.setPriority(Request.Priority.NORMAL);
        request1.setStatus(Request.RequestStatus.PENDING);
        request1.setRequestSupplies("Life jackets, rope");
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
        request2.setPriority(Request.Priority.NORMAL);
        request2.setStatus(Request.RequestStatus.IN_PROGRESS);
        request2.setRequestSupplies("Boat, medical kit");
        request2.setCreatedAt(LocalDateTime.now().minusMinutes(45));
        requestRepository.save(request2);

        // Request 3 - PENDING (classified as relief)
        Request request3 = new Request();
        request3.setUser(citizen);
        request3.setPhone(citizen.getPhoneNumber());
        request3.setRequestType(Request.RequestType.RESCUE);
        request3.setLatitude(new BigDecimal("10.823099"));
        request3.setLongitude(new BigDecimal("106.629662"));
        request3.setDescription("Community center needs food and water supplies.");
        request3.setPriority(Request.Priority.MEDIUM);
        request3.setStatus(Request.RequestStatus.PENDING);
        request3.setRequestSupplies("Bottled water, instant noodles");
        request3.setCreatedAt(LocalDateTime.now().minusMinutes(30));

        // Sample classified request
        User admin = userRepository.findByEmail("admin@floodrescue.com").orElse(null);
        if (admin != null) {
            request3.setClassifiedAt(LocalDateTime.now().minusMinutes(20));
            request3.setClassifiedBy(admin);
            request3.setPriority(Request.Priority.NORMAL);
            request3.setRequestType(Request.RequestType.RELIEF);
        }

        requestRepository.save(request3);
    }

    private void seedVehicles() {
        if (vehicleRepository.count() > 0) {
            return;
        }

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

    // =====================================================================
    // Core: Seed 22 HCM City Warehouses + Rescue Teams in 5 Strategic Districts
    // =====================================================================
    private void seedHcmData() {
        if (warehouseRepository.count() > 0) {
            return;
        }

        User admin = userRepository.findByEmail("admin@floodrescue.com")
                .orElseThrow(() -> new IllegalStateException(
                        "Admin user not found. Ensure users are seeded before warehouses."));

        Role rescueTeamRole = roleRepository.findByName("RESCUE_TEAM")
                .orElseThrow(() -> new IllegalStateException(
                        "RESCUE_TEAM role not found. Ensure roles are seeded first."));

        String encodedPassword = passwordEncoder.encode("team123");

        for (int d = 0; d < HCM_DISTRICTS.length; d++) {
            String districtName = HCM_DISTRICTS[d][0];
            BigDecimal lat = new BigDecimal(HCM_DISTRICTS[d][1]);
            BigDecimal lng = new BigDecimal(HCM_DISTRICTS[d][2]);
            String address = HCM_DISTRICTS[d][3];
            String slug = HCM_DISTRICTS[d][4];

            // --- Create Warehouse ---
            Warehouse warehouse = new Warehouse();
            warehouse.setUser(admin);
            warehouse.setResourceId("WH-" + String.format("%03d", d + 1));
            warehouse.setSupplyId("SUP-" + slug.toUpperCase());
            warehouse.setStatus(Warehouse.WarehouseStatus.ACTIVE);
            warehouse.setLatitude(lat);
            warehouse.setLongitude(lng);
            warehouse.setAddress(address);
            Warehouse savedWarehouse = warehouseRepository.save(warehouse);

            // --- Create Rescue Teams (only for strategic districts) ---
            if (STRATEGIC_DISTRICTS.contains(slug)) {
                for (int t = 1; t <= TEAMS_PER_STRATEGIC_DISTRICT; t++) {
                    RescueTeam team = new RescueTeam();
                    team.setName("Đội Cứu hộ " + districtName + " - " + t);
                    team.setStatus(RescueTeam.TeamStatus.ACTIVE);
                    team.setQuantity(MEMBERS_PER_TEAM);
                    team.setWarehouse(savedWarehouse);
                    RescueTeam savedTeam = rescueTeamRepository.save(team);

                    TeamPosition position = new TeamPosition();
                    position.setTeam(savedTeam);
                    position.setLatitude(lat);
                    position.setLongitude(lng);
                    position.setRecordedAt(LocalTime.now());
                    teamPositionRepository.save(position);

                    // --- Create 7 Team Members ---
                    for (int m = 1; m <= MEMBERS_PER_TEAM; m++) {
                        String email = String.format("rescue.%s.t%d.m%d@floodrescue.com", slug, t, m);
                        String phone = String.format("09%02d%d%d", d + 1, t, m);
                        String fullName = String.format("Thành viên %d - Đội %d %s", m, t, districtName);

                        if (!userRepository.existsByEmail(email)) {
                            User member = new User();
                            member.setFullName(fullName);
                            member.setEmail(email);
                            member.setPhoneNumber(phone);
                            member.setPasswordHash(encodedPassword);
                            member.setRole(rescueTeamRole);
                            member.setIsActive(true);
                            member.setCreatedAt(LocalDateTime.now());
                            User savedUser = userRepository.save(member);

                            TeamMember teamMember = new TeamMember();
                            teamMember.setUser(savedUser);
                            teamMember.setRescueTeam(savedTeam);
                            teamMember.setRoleInTeam(m == 1 ? "LEADER" : "MEMBER");
                            teamMemberRepository.save(teamMember);
                        }
                    }
                }
            }
        }
    }

    private void seedMissions() {
        if (missionRepository.count() > 0) {
            return;
        }

        Request baseRequest = requestRepository.findAll().stream().findFirst().orElse(null);
        if (baseRequest == null) {
            return;
        }

        Mission mission = new Mission();
        mission.setRequest(baseRequest);
        mission.setMissionType(Mission.MissionType.RESCUE);
        mission.setStatus(Mission.MissionStatus.IN_PROGRESS);
        mission.setStartTime(LocalDateTime.now().minusMinutes(30));
        mission.setCreatedAt(LocalDateTime.now().minusMinutes(40));

        missionRepository.save(mission);
    }

    private void seedInventoryAndReliefDistribution() {
        if (reliefDistributionRepository.count() > 0) {
            return;
        }

        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst().orElse(null);
        if (warehouse == null) {
            return;
        }

        Mission mission = missionRepository.findAll().stream().findFirst().orElse(null);
        if (mission == null) {
            return;
        }

        // Create sample item and inventory
        Item item = new Item();
        item.setName("Gạo cứu trợ");
        item.setItemType(Item.ItemType.FOOD);
        item.setCapacity("10kg");
        item.setStatus(Item.ItemStatus.ACTIVE);
        Item savedItem = itemRepository.save(item);

        Inventory inventory = new Inventory();
        inventory.setItem(savedItem);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(100);
        inventory.setLastUpdate(LocalDateTime.now());
        Inventory savedInventory = inventoryRepository.save(inventory);

        User admin = userRepository.findByEmail("admin@floodrescue.com").orElse(null);

        ReliefDistribution distribution = new ReliefDistribution();
        distribution.setMission(mission);
        distribution.setInventory(savedInventory);
        distribution.setQuantityDistributed(10);
        distribution.setHouseholdIdentifier("012345678901");
        distribution.setIsConfirmed(false);
        distribution.setRecordedBy(admin);
        distribution.setDistributedAt(LocalDateTime.now().minusMinutes(10));

        reliefDistributionRepository.save(distribution);
    }

    private void seedMissionReport() {
        if (reportRepository.count() > 0) {
            return;
        }

        Mission mission = missionRepository.findAll().stream().findFirst().orElse(null);
        if (mission == null) {
            return;
        }

        User teamUser = userRepository.findByEmail("team@rescue.com").orElse(null);
        if (teamUser == null) {
            return;
        }

        Report report = new Report();
        report.setMission(mission);
        report.setUser(teamUser);
        report.setPeopleRescued(5);
        report.setSummary("Đã giải cứu 5 người khỏi khu vực ngập lụt.");
        report.setObstacles("Đường ngập sâu, di chuyển khó khăn.");
        report.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        reportRepository.save(report);

        mission.setStatus(Mission.MissionStatus.COMPLETED);
        mission.setEndTime(LocalDateTime.now());
        missionRepository.save(mission);
    }
}
