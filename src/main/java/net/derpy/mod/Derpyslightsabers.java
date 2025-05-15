package net.derpy.mod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Derpyslightsabers implements ModInitializer {
	public static final String MOD_ID = "derpyslightsabers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


		LOGGER.info("Hello Fabric world!");
	}
}