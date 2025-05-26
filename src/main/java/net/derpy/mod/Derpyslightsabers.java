package net.derpy.mod;

import bond.thematic.mod.Thematic;
import net.derpy.mod.collection.LightsaberCollection;
import net.derpy.mod.entity.ModEntities;         // <-- Import ModEntities
import net.derpy.mod.item.ModItemGroups;
import net.derpy.mod.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoItem;

public class Derpyslightsabers implements ModInitializer {
	public static final String MOD_ID = "derpyslightsabers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Step 1: Initialize GeckoLib
		GeckoLib.initialize();

		// Step 2: Register all mod items
		ModItems.registerModItems();

		// Register all mod entities (including attributes)
		ModEntities.registerModEntities();      // <-- Add this call here!

		// For mod item groups
		ModItemGroups.registerItemGroups();

		// Step 3: Register synced animatable for GeckoLib to sync animations
		// Make sure ANAKINSLIGHTSABER is actually an instance of a GeoAnimatable item (which it is)
		GeoItem.registerSyncedAnimatable(ModItems.ANAKINS_LIGHTSABER);

		LOGGER.info("Derpy's Lightsabers mod initialized successfully!");

		// Add thematic armor
		LightsaberCollection lightsaberCollection = new LightsaberCollection();
		Thematic.addCollection(lightsaberCollection);
		lightsaberCollection.initServer();
	}
}
