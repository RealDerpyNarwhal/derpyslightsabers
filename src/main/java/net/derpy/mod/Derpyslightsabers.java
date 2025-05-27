package net.derpy.mod;

import bond.thematic.mod.Thematic;
import net.derpy.mod.collection.LightsaberCollection;
import net.derpy.mod.entity.ModEntities;
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

		// Step 3: Register all mod entities and their attributes
		ModEntities.registerModEntities();  // ✅ Attribute registration included here

		// Step 4: Register item groups
		ModItemGroups.registerItemGroups();

		// Step 5: Register synced animatable items
		GeoItem.registerSyncedAnimatable(ModItems.ANAKINS_LIGHTSABER);

		// Step 6: Initialize Thematic content
		LightsaberCollection lightsaberCollection = new LightsaberCollection();
		Thematic.addCollection(lightsaberCollection);
		lightsaberCollection.initServer();

		LOGGER.info("Derpy's Lightsabers mod initialized successfully!");
	}
}
