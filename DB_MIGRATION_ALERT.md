# 🚨 DATABASE MIGRATION ALERT 🚨

**Attention Backend Team:**
The `RescueTeam` entity has been updated to include a relationship with `Warehouse`.

## Schema Changes
The following new fields have been added to the `RescueTeam` table/entity:
- `warehouse`: Many-to-One relationship mapping to a `Warehouse` entity (`warehouse_id` Foreign Key).

## Required Actions
To avoid application startup errors or database constraint failures, please perform one of the following actions:

1. **Option 1 (Recommended for local dev):** Change your application properties to use `spring.jpa.hibernate.ddl-auto=update` temporarily to let Hibernate migrate the schema. 
2. **Option 2:** Drop and recreate your local database schema (or use `ddl-auto=create`). Note: This will wipe your existing local data.
3. **Option 3:** Backup your current local database and run the appropriate `ALTER TABLE` scripts to add `warehouse_id` field to the `rescue_team` table.

Please make sure to sync up with these changes before continuing development, especially if you rely on `RescueTeam` or `Warehouse` data locally!
