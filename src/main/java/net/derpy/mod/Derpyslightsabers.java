package net.derpy.mod;

import bond.thematic.mod.Thematic;
import net.derpy.mod.collection.LightsaberCollection;
import net.derpy.mod.collection.PsychicCollection;
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
		// Initialize GeckoLib
		GeckoLib.initialize();

		// Register all mod items
		ModItems.registerModItems();

		// Register all mod entities and their attributes
		ModEntities.registerModEntities();

		// Register item groups
		ModItemGroups.registerItemGroups();

		// Register synced animatable items
		GeoItem.registerSyncedAnimatable(ModItems.ANAKINS_LIGHTSABER);

		// Register LightsaberCollection
		LightsaberCollection lightsaberCollection = new LightsaberCollection();
		Thematic.addCollection(lightsaberCollection);
		lightsaberCollection.initServer();

		PsychicCollection psychicCollection = new PsychicCollection();
		Thematic.addCollection(psychicCollection);
		psychicCollection.initServer();

		LOGGER.info("Derpy's Lightsabers mod initialized successfully!");
	}
}
