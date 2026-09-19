package com.systemdesignlab.platform.database;

/**
 * The least-privilege module DB roles created by
 * {@code database/migrations/V002__create_module_roles_and_grants.sql} (ADR-018).
 *
 * <p>Test-only registry so the schema-isolation and migration tests do not duplicate
 * the migration's role names, passwords and owning schemas.
 */
enum ModuleDatabaseRole {

    CUSTOMER("customer_app", "customer_app_dev_only_pw", "customer"),
    CATALOG("catalog_app", "catalog_app_dev_only_pw", "catalog"),
    CART("cart_app", "cart_app_dev_only_pw", "cart"),
    ORDERING("ordering_app", "ordering_app_dev_only_pw", "ordering"),
    INVENTORY("inventory_app", "inventory_app_dev_only_pw", "inventory"),
    PAYMENT("payment_app", "payment_app_dev_only_pw", "payment");

    final String username;
    final String password;
    final String ownSchema;

    ModuleDatabaseRole(String username, String password, String ownSchema) {
        this.username = username;
        this.password = password;
        this.ownSchema = ownSchema;
    }

    @Override
    public String toString() {
        return ownSchema;
    }
}
